package com.batodev.tetris.domain.game.logs

import com.batodev.tetris.presentation.common.UiText

interface Logger {
    fun add(message: UiText)
    fun getLog(): List<UiText>
    fun clear()
}