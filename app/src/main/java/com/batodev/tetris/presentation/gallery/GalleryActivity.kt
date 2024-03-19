package com.batodev.tetris.presentation.gallery

import android.animation.Animator
import android.animation.AnimatorInflater
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

    fun backClicked(view: View) {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(this)
        }
        finish()
    }

    fun leftClicked(view: View) {
        if (index != 0) index--
        animateTileFlip(findViewById<PhotoView>(R.id.photoView), this, true)
        val settingsData = SettingsHelper.load(this)
        indexUpdate(settingsData)
        AdHelper.showAddIfNeeded(this, settingsData)
        RateAppHelper.increaseRateAppCounterAndShowDialogIfApplicable(this, settingsData)
        checkIfImageLeftRightButtonsShouldBeVisible()
        SettingsHelper.save(this, settingsData)
    }

    fun rightClicked(view: View) {
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

    fun shareClicked(view: View) {
        val currentImageName = images[index]
        val inputStream = ImageHelper.imageStreamByName(this, currentImageName)

        val tmpImgPath = "tmp_shared/tmp.jpg"
        val file = File(filesDir, tmpImgPath)
        File(filesDir, "tmp_shared").mkdirs()
        file.delete()
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
        val shareIntent = Intent(Intent.ACTION_SEND)
        val uri =
            Uri.parse("content://com.batodev.beautifulasiangirlpics.ImagesProvider/$tmpImgPath")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        ContextCompat.startActivity(this, shareIntent, null)
    }

    private fun animateTileFlip(
        tileView: View,
        gameActivity: GalleryActivity,
        rightClicked: Boolean
    ) {
        val imgFlipA = if (rightClicked) R.animator.img_flip_a else R.animator.img_flip_c
        val imgFlipB = if (rightClicked) R.animator.img_flip_b else R.animator.img_flip_d
        val rotateFlipAnimationA =
            AnimatorInflater.loadAnimator(this, imgFlipA) as AnimatorSet
        rotateFlipAnimationA.setTarget(tileView)
        rotateFlipAnimationA.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                val rotateFlipAnimationB = AnimatorInflater.loadAnimator(
                    gameActivity,
                    imgFlipB
                ) as AnimatorSet
                rotateFlipAnimationB.setTarget(tileView)
                rotateFlipAnimationB.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(p0: Animator) {}
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
                setImage(index)
                rotateFlipAnimationB.start()
            }
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        rotateFlipAnimationA.start()
    }
}