package com.batodev.tetris.presentation.finished

import java.io.Serializable

data class FinishedStateData(
    val email: String = "",
    val emailError: String? = null
): Serializable