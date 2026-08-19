package com.batodev.tetris.presentation.game

import GameFacade
import androidx.lifecycle.MutableLiveData
import com.batodev.tetris.infra.logs.LoggerConstants
import com.batodev.tetris.infra.logs.LoggerGetter
import com.batodev.tetris.presentation.common.UiText

/**
 * Applies a directional/rotation move to the current [GameFacade] if the game
 * isn't paused or finished, logging it and marking the shared "updated" flag -
 * split out of [GameViewModel] since the six moves were the bulk of its
 * function count.
 */
class MovementActions(
    private val gameFacade: MutableLiveData<GameFacade>,
    private val updatedLog: MutableLiveData<Boolean>,
    private val isPaused: () -> Boolean,
) {
    fun left() = applyMove(LoggerConstants.MOVE_LEFT) { it.left() }
    fun right() = applyMove(LoggerConstants.MOVE_RIGHT) { it.right() }
    fun down() = applyMove(LoggerConstants.MOVE_DOWN) { it.down() }
    fun rotateLeft() = applyMove(LoggerConstants.ROTATE_LEFT) { it.rotateLeft() }
    fun rotateRight() = applyMove(LoggerConstants.ROTATE_RIGHT) { it.rotateRight() }
    fun dropBlock() = applyMove(LoggerConstants.DROP_DOWN) { it.dropBlock() }

    private fun applyMove(logEvent: UiText, action: (GameFacade) -> Unit) {
        if (validMovement()) {
            action(gameFacade.value!!)
            gameFacade.postValue(gameFacade.value)
            LoggerGetter.get().add(logEvent)
            updatedLog.value = true
        }
    }

    private fun validMovement() = !isPaused() && !gameFacade.value!!.hasFinished()
}
