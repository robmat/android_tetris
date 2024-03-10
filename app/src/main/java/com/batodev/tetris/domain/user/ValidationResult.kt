package com.batodev.tetris.domain.user

import com.batodev.tetris.presentation.common.UiText

data class ValidationResult(
    val success: Boolean,
    val errorMessage: UiText?=null
)
