package com.batodev.tetris.domain.game.speed

import score.Points

class MediumSpeed : SpeedStrategy {

    companion object {
        private val THRESHOLDS = listOf(
            SpeedThreshold(upperBound = 200, speedMs = 1000L),
            SpeedThreshold(upperBound = 600, speedMs = 900L),
            SpeedThreshold(upperBound = 800, speedMs = 800L),
            SpeedThreshold(upperBound = 1000, speedMs = 700L),
            SpeedThreshold(upperBound = 1200, speedMs = 600L),
            SpeedThreshold(upperBound = 1400, speedMs = 550L),
            SpeedThreshold(upperBound = 1600, speedMs = 500L)
        )
        private const val DEFAULT_SPEED_MS = 400L
    }

    override fun getSpeedInMilliseconds(points: Points) =
        speedInMillisecondsFromThresholds(points, THRESHOLDS, DEFAULT_SPEED_MS)
}
