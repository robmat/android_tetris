package com.batodev.tetris.presentation.game

import android.os.Bundle
import com.batodev.tetris.R
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.common.HideStatusBarActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class GameActivity : HideStatusBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        SettingsHelper.save(this, SettingsHelper.load(this))
    }
}