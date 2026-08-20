package com.batodev.tetris.domain.game.speed

import score.Points

class LowSpeed : SpeedStrategy {

    companion object {
        private val THRESHOLDS = listOf(
            SpeedThreshold(upperBound = 200, speedMs = 1000L),
            SpeedThreshold(upperBound = 600, speedMs = 900L),
            SpeedThreshold(upperBound = 1000, speedMs = 800L),
            SpeedThreshold(upperBound = 1300, speedMs = 700L),
            SpeedThreshold(upperBound = 1500, speedMs = 600L),
            SpeedThreshold(upperBound = 1800, speedMs = 500L),
            SpeedThreshold(upperBound = 2000, speedMs = 400L)
        )
        private const val DEFAULT_SPEED_MS = 350L
    }

    override fun getSpeedInMilliseconds(points: Points) =
        speedInMillisecondsFromThresholds(points, THRESHOLDS, DEFAULT_SPEED_MS)
}
