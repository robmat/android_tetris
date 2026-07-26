package com.batodev.tetris.domain.game.speed

import score.Points

class HighSpeed : SpeedStrategy {

    companion object {
        private const val THRESHOLD_1 = 200
        private const val THRESHOLD_2 = 500
        private const val THRESHOLD_3 = 700
        private const val THRESHOLD_4 = 1000
        private const val THRESHOLD_5 = 1200
        private const val THRESHOLD_6 = 1400
        private const val SPEED_MS_1 = 1000L
        private const val SPEED_MS_2 = 900L
        private const val SPEED_MS_3 = 800L
        private const val SPEED_MS_4 = 600L
        private const val SPEED_MS_5 = 550L
        private const val SPEED_MS_6 = 450L
        private const val SPEED_MS_7 = 350L
    }

    override fun getSpeedInMilliseconds(points: Points) = when (points.value) {
        in 0 until THRESHOLD_1 -> SPEED_MS_1
        in THRESHOLD_1 until THRESHOLD_2 -> SPEED_MS_2
        in THRESHOLD_2 until THRESHOLD_3 -> SPEED_MS_3
        in THRESHOLD_3 until THRESHOLD_4 -> SPEED_MS_4
        in THRESHOLD_4 until THRESHOLD_5 -> SPEED_MS_5
        in THRESHOLD_5 until THRESHOLD_6 -> SPEED_MS_6
        else -> SPEED_MS_7
    }
}
