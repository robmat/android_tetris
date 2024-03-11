package com.batodev.tetris.infra.images;

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF

import com.batodev.tetris.infra.settings.SettingsHelper

object ImageHelper {
    fun pickTierOneImage(activity: Activity): ImageData {
        return pickImage(activity, "pics/tier1")
    }

    fun pickTierTwoImage(activity: Activity): ImageData {
        return pickImage(activity, "pics/tier2")
    }

    fun pickTierThreeImage(activity: Activity): ImageData {
        return pickImage(activity, "pics/tier3")
    }

    private fun pickImage(
        activity: Activity,
        imagesPath: String,
    ): ImageData {
        val assetImages = activity.assets.list(imagesPath)!!
        val uncoveredImages = SettingsHelper.load(activity).imagesWon
        val imagesToPickFrom = assetImages.filter { uncoveredImages.contains(it) }
        val imageName =
            if (imagesToPickFrom.isEmpty()) assetImages.random() else imagesToPickFrom.random()
        return ImageData(
            BitmapFactory.decodeStream(activity.assets.open("$imagesPath/$imageName")),
            imageName
        )
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
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
