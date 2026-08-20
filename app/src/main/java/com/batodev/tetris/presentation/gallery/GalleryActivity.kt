package com.batodev.tetris.presentation.gallery

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import com.batodev.tetris.R
import com.batodev.tetris.infra.helpers.AdHelper
import com.batodev.tetris.infra.helpers.RateAppHelper
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.infra.settings.SettingsData
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.main.MainActivity
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

const val IMAGES = "images"

class GalleryActivity : Activity() {
    companion object {
        private const val COPY_BUFFER_SIZE = 1024
    }

    private var images: Array<String> = listOf<String>().toTypedArray()
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.galery_activity)
        this.images = this.intent.extras!!.getStringArray(IMAGES)!!
        index = SettingsHelper.load(this).lastSeenGalleryImageIndex
        setImage(index)
        findViewById<AdView>(R.id.gallery_ad).loadAd(AdRequest.Builder().build())
        checkIfImageLeftRightButtonsShouldBeVisible()
    }

    fun backClicked(ignored: View) {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(this)
        }
        finish()
    }

    fun leftClicked(ignored: View) {
        if (index != 0) index--
        animateTileFlip(findViewById<PhotoView>(R.id.photoView), this, true)
        val settingsData = SettingsHelper.load(this)
        indexUpdate(settingsData)
        AdHelper.showAddIfNeeded(this, settingsData)
        RateAppHelper.increaseRateAppCounterAndShowDialogIfApplicable(this, settingsData)
        checkIfImageLeftRightButtonsShouldBeVisible()
        SettingsHelper.save(this, settingsData)
    }

    fun rightClicked(ignored: View) {
        if (index < images.size) index++
        animateTileFlip(findViewById<PhotoView>(R.id.photoView), this, false)
        val settingsData = SettingsHelper.load(this)
        indexUpdate(settingsData)
        AdHelper.showAddIfNeeded(this, settingsData)
        RateAppHelper.increaseRateAppCounterAndShowDialogIfApplicable(this, settingsData)
        checkIfImageLeftRightButtonsShouldBeVisible()
        SettingsHelper.save(this, settingsData)
    }

    private fun indexUpdate(settingsData: SettingsData) {
        settingsData.lastSeenGalleryImageIndex = index
    }

    private fun checkIfImageLeftRightButtonsShouldBeVisible() {
        if (index <= 0) {
            findViewById<Button>(R.id.gallery_left).visibility = View.GONE
        } else {
            findViewById<Button>(R.id.gallery_left).visibility = View.VISIBLE
        }
        if (index >= images.size - 1) {
            findViewById<Button>(R.id.gallery_right).visibility = View.GONE
        } else {
            findViewById<Button>(R.id.gallery_right).visibility = View.VISIBLE
        }
    }

    private fun setImage(index: Int) {
        if (index >= 0 && index < images.size) {
            findViewById<PhotoView>(R.id.photoView)
                .setImageBitmap(ImageHelper.imageBitmapByName(this, images[index]))
            this.index = index
        } else {
            setImage(index - 1)
        }
    }

    fun shareClicked(ignored: View) {
        val currentImageName = images[index]
        val inputStream = ImageHelper.imageStreamByName(this, currentImageName)

        val tmpImgPath = "tmp_shared/tmp.jpg"
        val file = File(filesDir, tmpImgPath)
        File(filesDir, "tmp_shared").mkdirs()
        file.delete()
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(COPY_BUFFER_SIZE)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
        val shareIntent = Intent(Intent.ACTION_SEND)
        val uri =
            Uri.parse("content://com.batodev.tetris.ImagesProvider/$tmpImgPath")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.clipData = android.content.ClipData.newRawUri("", uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = "image/*"
        ContextCompat.startActivity(this, shareIntent, null)
    }

    private fun animateTileFlip(
        tileView: View,
        gameActivity: GalleryActivity,
        rightClicked: Boolean,
    ) {
        val imgFlipA = if (rightClicked) R.animator.img_flip_a else R.animator.img_flip_c
        val imgFlipB = if (rightClicked) R.animator.img_flip_b else R.animator.img_flip_d
        val rotateFlipAnimationA =
            AnimatorInflater.loadAnimator(this, imgFlipA) as AnimatorSet
        rotateFlipAnimationA.setTarget(tileView)
        rotateFlipAnimationA.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val rotateFlipAnimationB =
                        AnimatorInflater.loadAnimator(
                            gameActivity,
                            imgFlipB,
                        ) as AnimatorSet
                    rotateFlipAnimationB.setTarget(tileView)
                    setImage(index)
                    rotateFlipAnimationB.start()
                }
            },
        )
        rotateFlipAnimationA.start()
    }
}
