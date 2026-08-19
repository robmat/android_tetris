package com.batodev.tetris.presentation.game

import androidx.lifecycle.MutableLiveData

/**
 * Owns the "has the game started" / "is it currently paused" flags - split
 * out of [GameViewModel].
 */
class GameStateFlags {
    private val gamePaused: MutableLiveData<Boolean> = MutableLiveData(false)
    private val gameOpened: MutableLiveData<Boolean> = MutableLiveData(false)

    fun isGameStarted() = gameOpened.value!!

    fun setGameStarted() {
        gameOpened.value = true
    }

    fun isGamePaused() = gamePaused.value!!

    fun setGamePaused() {
        gamePaused.value = true
    }

    fun setGameResume() {
        gamePaused.value = false
    }
}
