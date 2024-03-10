package com.batodev.tetris.presentation.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batodev.tetris.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.settingsActivityBack.setOnClickListener { finish() }
    }
}