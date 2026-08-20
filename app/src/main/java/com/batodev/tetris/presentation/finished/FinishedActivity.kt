package com.batodev.tetris.presentation.finished

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.batodev.tetris.R
import com.batodev.tetris.databinding.ActivityFinishedBinding
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.presentation.common.HideStatusBarActivity
import com.batodev.tetris.presentation.common.openUnlockedGalleryOrPromptToPlay
import com.batodev.tetris.presentation.game.GameActivity
import com.batodev.tetris.presentation.game.fragments.IMAGES_WON_THIS_GAME

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
            startActivity(
                Intent(this, GameActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            finish()
        }
        findViewById<Button>(R.id.finished_activity_unlocked_gallery).setOnClickListener {
            openUnlockedGalleryOrPromptToPlay(binding.root)
            finish()
        }
    }
}
