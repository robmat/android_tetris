package com.batodev.tetris.infra.logs

import com.batodev.tetris.R
import com.batodev.tetris.domain.game.logs.Logger
import com.batodev.tetris.infra.logs.LoggerConstants.DROP_DOWN
import com.batodev.tetris.infra.logs.LoggerConstants.MOVE_DOWN
import com.batodev.tetris.infra.logs.LoggerConstants.MOVE_LEFT
import com.batodev.tetris.infra.logs.LoggerConstants.MOVE_RIGHT
import com.batodev.tetris.infra.logs.LoggerConstants.ROTATE_LEFT
import com.batodev.tetris.infra.logs.LoggerConstants.ROTATE_RIGHT
import com.batodev.tetris.presentation.common.UiText

object MemoryLogger : Logger {

    private val messages: MutableList<UiText> = mutableListOf()
    private val movements: MutableMap<UiText, Int> = mutableMapOf()

    override fun add(message: UiText) {
        when (message) {
            MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT, DROP_DOWN -> movements[message] =
                movements.getOrElse(message) { 0 }.plus(1)
            else -> messages.add(message)
        }
    }

    override fun getLog(): List<UiText> {
        val result = messages.toMutableList()
        result.addAll(movements.map {
            UiText.ResourceString(
                R.string.result_movements_log,
                it.key,
                it.value
            )
        })
        return result
    }

    override fun clear() {
        messages.clear()
        movements.clear()
    }

}