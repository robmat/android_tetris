package com.batodev.tetris.domain.user

import android.util.Patterns
import com.batodev.tetris.R
import com.batodev.tetris.presentation.common.UiText

class ValidateEmail(private val email: String) : Validator {

    override fun execute(): ValidationResult = when {
        email.isBlank() -> ValidationResult(false, UiText.ResourceString(R.string.error_email_empty))
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            ValidationResult(false, UiText.ResourceString(R.string.error_email_not_valid))
        else -> ValidationResult(true)
    }
}
