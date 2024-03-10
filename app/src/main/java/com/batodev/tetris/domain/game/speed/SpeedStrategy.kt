package com.batodev.tetris.domain.game.speed

import score.Points

interface SpeedStrategy {
    fun getSpeedInMilliseconds(points: Points): Long
}