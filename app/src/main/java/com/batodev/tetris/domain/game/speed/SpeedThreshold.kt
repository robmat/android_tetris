package com.batodev.tetris.domain.game.speed

import score.Points

/**
 * A single breakpoint used by [speedInMillisecondsFromThresholds]. The
 * associated [speedMs] applies while the points value is strictly below
 * [upperBound].
 */
data class SpeedThreshold(val upperBound: Int, val speedMs: Long)

/**
 * Looks up the game speed for [points] using an ascending list of
 * [thresholds]. The speed of the first threshold whose [SpeedThreshold.upperBound]
 * is greater than the current points value is returned; if none match,
 * [defaultSpeedMs] is used.
 */
fun speedInMillisecondsFromThresholds(
    points: Points,
    thresholds: List<SpeedThreshold>,
    defaultSpeedMs: Long
): Long = thresholds.firstOrNull { points.value < it.upperBound }?.speedMs ?: defaultSpeedMs
