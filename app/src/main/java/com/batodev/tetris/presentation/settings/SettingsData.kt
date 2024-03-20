package com.batodev.tetris.presentation.settings

import com.batodev.tetris.domain.game.Level
import com.batodev.tetris.presentation.game.grid.style.Style

data class SettingsData(
    val name: String = "",
    val isGhostBlock: Boolean = true,
    val hasMusic: Boolean = true,
    val hasSounds: Boolean = true,
    val style: Style = Style.NEON,
    val level: Level = Level.MEDIUM,
)