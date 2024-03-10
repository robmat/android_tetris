package com.batodev.tetris.infra.logs

import com.batodev.tetris.R
import com.batodev.tetris.domain.game.logs.Logger
import com.batodev.tetris.presentation.common.UiText

object LoggerGetter {
    fun get(): Logger = MemoryLogger
}

object LoggerConstants {
    val MOVE_DOWN = UiText.ResourceString(R.string.down_movement)
    val MOVE_LEFT = UiText.ResourceString(R.string.left_movement)
    val MOVE_RIGHT = UiText.ResourceString(R.string.right_movement)
    val ROTATE_LEFT = UiText.ResourceString(R.string.left_rotation)
    val ROTATE_RIGHT = UiText.ResourceString(R.string.right_rotation)
    val DROP_DOWN = UiText.ResourceString(R.string.drop_down)
}