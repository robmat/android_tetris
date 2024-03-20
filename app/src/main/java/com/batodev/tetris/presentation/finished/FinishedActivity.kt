package com.batodev.tetris.presentation.finished

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.batodev.tetris.R
import com.batodev.tetris.databinding.ActivityFinishedBinding
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.common.HideStatusBarActivity
import com.batodev.tetris.presentation.gallery.GalleryActivity
import com.batodev.tetris.presentation.gallery.IMAGES
import com.batodev.tetris.presentation.game.GameActivity
import com.batodev.tetris.presentation.game.fragments.IMAGES_WON_THIS_GAME
import com.google.android.material.snackbar.Snackbar


class FinishedActivity : HideStatusBarActivity() {

    private lateinit var binding: ActivityFinishedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getStringArray(IMAGES_WON_THIS_GAME)?.let {
            if (it.isNotEmpty()) {
                findViewById<ImageView>(R.id.won_image_1).setImageBitmap(ImageHelper.imageBitmapByName(this, it[0]))
            }
            if (it.size > 1) {
                findViewById<ImageView>(R.id.won_image_2).setImageBitmap(ImageHelper.imageBitmapByName(this, it[1]))
            }
            if (it.size > 2) {
                findViewById<ImageView>(R.id.won_image_3).setImageBitmap(ImageHelper.imageBitmapByName(this, it[2]))
            }
        }
        findViewById<Button>(R.id.finished_activity_play_the_game).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            finish()
        }
        findViewById<Button>(R.id.finished_activity_unlocked_gallery).setOnClickListener {
            val imagesWon = SettingsHelper.load(this).imagesWon
            if (imagesWon.isNotEmpty()) {
                val intent = Intent(this, GalleryActivity::class.java)
                intent.putExtra(IMAGES, imagesWon.toTypedArray())
                startActivity(intent)
            } else {
                val snackBar = Snackbar.make(binding.root, getString(R.string.playTheGameTounlockImages), Snackbar.LENGTH_SHORT)
                val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                snackBar.view.layoutParams = params
                snackBar.show()
            }
            finish()
        }
    }
}