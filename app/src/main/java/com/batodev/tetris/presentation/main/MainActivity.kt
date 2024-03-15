package com.batodev.tetris.presentation.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.batodev.tetris.R
import com.batodev.tetris.databinding.ActivityMainBinding
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.common.HideStatusBarActivity
import com.batodev.tetris.presentation.common.getButtons
import com.batodev.tetris.presentation.gallery.GalleryActivity
import com.batodev.tetris.presentation.gallery.IMAGES
import com.batodev.tetris.presentation.game.GameActivity
import com.batodev.tetris.presentation.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : HideStatusBarActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainModel = MainModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.getButtons().forEach { it.setOnClickListener(this) }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            binding.mainMenuActivityPlayTheGame.id -> startGameActivity()
            binding.mainMenuActivityMoreApps.id -> moreApps()
            binding.mainMenuActivitySettings.id -> settings()
            binding.mainMenuActivityUnlockedGallery.id -> gallery()
            else -> throw IllegalArgumentException("Unknown button id: ${p0.id}")
        }
    }

    private fun settings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun gallery() {
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
    }

    private fun moreApps() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8228670503574649511")))
    }

    private fun startGameActivity() {
        val game = Intent(this, GameActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(game)
    }
}