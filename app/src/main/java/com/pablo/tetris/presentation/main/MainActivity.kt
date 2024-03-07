package com.pablo.tetris.presentation.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pablo.tetris.databinding.ActivityMainBinding
import com.pablo.tetris.presentation.common.HideStatusBarActivity
import com.pablo.tetris.presentation.common.getButtons
import com.pablo.tetris.presentation.game.GameActivity
import com.pablo.tetris.presentation.settings.SettingsActivity

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

    }

    private fun moreApps() {

    }

    private fun startGameActivity() {
        val game = Intent(this, GameActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(game)
    }
}