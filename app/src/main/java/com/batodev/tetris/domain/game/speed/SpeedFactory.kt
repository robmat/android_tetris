package com.batodev.tetris.domain.game.speed

import com.batodev.tetris.domain.game.Level

object SpeedFactory {

    fun get(level: Level) = when (level) {
        Level.LOW -> LowSpeed()
        Level.MEDIUM -> MediumSpeed()
        Level.HIGH -> HighSpeed()
    }

}