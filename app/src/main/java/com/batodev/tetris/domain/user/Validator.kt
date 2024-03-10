package com.batodev.tetris.domain.user

interface Validator {
    fun execute(): ValidationResult
}