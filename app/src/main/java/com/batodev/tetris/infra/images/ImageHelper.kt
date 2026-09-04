package com.batodev.tetris.infra.images

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.batodev.tetris.infra.settings.SettingsHelper
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode

object ImageHelper {
    private const val ROUNDED_CORNER_RADIUS_PX = 25
    private const val ASPECT_RATIO_WIDTH = 275
    private const val ASPECT_RATIO_HEIGHT = 550
    private const val ASPECT_RATIO_SCALE = 5

    fun imageStreamByName(
        activity: Activity,
        name: String,
    ): InputStream {
        val tier =
            listOf("pics/tier1", "pics/tier2", "pics/tier3")
                .firstOrNull { activity.assets.list(it)!!.contains(name) }
                ?: throw NoSuchElementException("no image by name: $name")
        return activity.assets.open("$tier/$name")
    }

    fun imageBitmapByName(
        activity: Activity,
        name: String,
    ): Bitmap = BitmapFactory.decodeStream(imageStreamByName(activity, name))

    fun pickTierOneImage(activity: Activity): ImageData = pickImage(activity, "pics/tier1")

    fun pickTierTwoImage(activity: Activity): ImageData = pickImage(activity, "pics/tier2")

    fun pickTierThreeImage(activity: Activity): ImageData = pickImage(activity, "pics/tier3")

    private fun pickImage(
        activity: Activity,
        imagesPath: String,
    ): ImageData {
        val assetImages = activity.assets.list(imagesPath)!!
        val uncoveredImages = SettingsHelper.load(activity).imagesWon
        val imagesToPickFrom = assetImages.filter { !uncoveredImages.contains(it) }
        val imageName =
            if (imagesToPickFrom.isEmpty()) assetImages.random() else imagesToPickFrom.random()
        val originalBitmap = BitmapFactory.decodeStream(activity.assets.open("$imagesPath/$imageName"))
        val croppedBitmap = cropToAspectRatio(originalBitmap)
        val roundCornersBitmap = getRoundedCornerBitmap(croppedBitmap, ROUNDED_CORNER_RADIUS_PX)
        return ImageData(roundCornersBitmap, imageName)
    }

    private fun cropToAspectRatio(bitmap: Bitmap): Bitmap {
        val aspectRatio: BigDecimal =
            BigDecimal(ASPECT_RATIO_WIDTH)
                .divide(BigDecimal(ASPECT_RATIO_HEIGHT), ASPECT_RATIO_SCALE, RoundingMode.HALF_UP)
        val width = BigDecimal(bitmap.width)
        val height = BigDecimal(bitmap.height)

        val targetWidth: Int
        val targetHeight: Int

        // Calculate the dimensions of the cropped region based on the aspect ratio
        if (width / height > aspectRatio) {
            targetWidth = (height * aspectRatio).toInt()
            targetHeight = height.toInt()
        } else {
            targetWidth = width.toInt()
            targetHeight = (width / aspectRatio).toInt()
        }

        // Calculate the coordinates of the top-left corner of the cropped region
        val left = (width.toInt() - targetWidth) / 2
        val top = (height.toInt() - targetHeight) / 2

        // Create a Rect object representing the cropping region
        val rect = Rect(left, top, left + targetWidth, top + targetHeight)

        // Crop the bitmap to the specified region
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }

    fun getRoundedCornerBitmap(
        bitmap: Bitmap,
        pixels: Int,
    ): Bitmap {
        val output =
            createBitmap(
                bitmap.width,
                bitmap.height,
            )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, pixels.toFloat(), pixels.toFloat(), paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}
