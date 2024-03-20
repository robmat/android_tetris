package com.batodev.tetris.presentation.settings

import GameFacade
import android.content.Context
import androidx.preference.PreferenceManager
import com.batodev.tetris.R
import com.batodev.tetris.domain.game.Level
import com.batodev.tetris.domain.game.blocks.BlockGeneratorFactory
import com.batodev.tetris.domain.game.speed.SpeedFactory
import com.batodev.tetris.domain.game.speed.SpeedStrategy
import com.batodev.tetris.infra.logs.LoggerGetter
import com.batodev.tetris.presentation.common.UiText
import com.batodev.tetris.presentation.game.grid.style.Style
import com.batodev.tetris.presentation.game.grid.style.StyleCreator
import com.batodev.tetris.presentation.game.grid.style.StyleFactory
import java.io.Serializable

object SettingsSingleton : Serializable {
    private fun readResolve(): Any = SettingsSingleton

    fun getSettingsData(context: Context): SettingsData {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString(context.getString(R.string.key_name), context.getString(R.string.name_default))
        val isGhost = preferences.getBoolean(context.getString(R.string.key_ghost), true)
        val hasMusic = preferences.getBoolean(context.getString(R.string.key_music), true)
        val hasSounds = preferences.getBoolean(context.getString(R.string.key_sounds), true)
        val difficultyString = preferences.getString(context.getString(R.string.key_difficulty), context.getString(R.string.medium_key))
        val themeString = preferences.getString(context.getString(R.string.key_theme), context.getString(R.string.neon_key))
        val level = Level.valueOf(difficultyString!!)
        val theme = Style.valueOf(themeString!!)
        return SettingsData(
            name = name!!,
            isGhostBlock = isGhost,
            hasMusic = hasMusic,
            hasSounds = hasSounds,
            level = level,
            style = theme
        )
    }

    fun getFacade(context: Context): GameFacade {
        val settings = getSettingsData(context)
        val generator = BlockGeneratorFactory.getGenerator(settings.level)
        return GameFacade(blockGenerator = generator, ghost = settings.isGhostBlock)
    }

    fun getStyleCreator(context: Context): StyleCreator {
        val settings = getSettingsData(context)
        return StyleFactory.getStyleCreator(settings.style, context)
    }

    fun getSpeedStrategy(context: Context): SpeedStrategy {
        val settings = getSettingsData(context)
        return SpeedFactory.get(settings.level)
    }

    fun logData(context: Context) {
        val settings = getSettingsData(context)
        LoggerGetter.get().add(
            UiText.ResourceString(R.string.player_name_log, settings.name),
        )
        LoggerGetter.get().add(
            UiText.ResourceString(R.string.level_selected_log, settings.level.name)
        )
        LoggerGetter.get().add(
            UiText.ResourceString(R.string.ghost_mode_log, settings.isGhostBlock)
        )
        LoggerGetter.get().add(
            UiText.ResourceString(R.string.music_mode_log, settings.hasMusic)
        )
        LoggerGetter.get()
            .add(
                UiText.ResourceString(R.string.theme_log, settings.style.name)
            )
    }

}