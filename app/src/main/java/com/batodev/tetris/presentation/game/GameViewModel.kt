package com.batodev.tetris.presentation.game

import GameFacade
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batodev.tetris.domain.game.speed.SpeedStrategy
import kotlinx.coroutines.delay

class GameViewModel : ViewModel() {

    val gameFacade: MutableLiveData<GameFacade> = MutableLiveData(null)
    private val updatedLog: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var speedStrategy: SpeedStrategy
    private var imageName: MutableLiveData<String> = MutableLiveData("")

    val state = GameStateFlags()
    val music = MusicController()
    val movement = MovementActions(gameFacade, updatedLog) { state.isGamePaused() }

    fun setUp(gameFacade: GameFacade, speed: SpeedStrategy) {
        if (this.gameFacade.value == null) {
            gameFacade.start()
            this.gameFacade.value = gameFacade
        }
        this.speedStrategy = speed
    }

    suspend fun runGame() {
        Log.d(GameViewModel::class.java.simpleName, "run game")
        while (!gameFacade.value!!.hasFinished()) {
            movement.down()
            delay(speedStrategy.getSpeedInMilliseconds(gameFacade.value!!.getScore()))
        }
    }

    fun getGrid() = gameFacade.value!!.getGrid().flatMap { it.toList() }

    fun getNextBlock() = gameFacade.value!!.getNextBlock()

    fun getPoints() = gameFacade.value!!.getScore().value

    fun setUpImage(fileName: String) {
        imageName.value = fileName
    }
}
