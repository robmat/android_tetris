package com.batodev.tetris.domain.user

import android.util.Patterns
import com.batodev.tetris.R
import com.batodev.tetris.presentation.common.UiText

class ValidateEmail(private val email: String): Validator {

    override fun execute(): ValidationResult {
        if(email.isBlank())
            return ValidationResult(false, UiText.ResourceString(R.string.error_email_empty))
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return ValidationResult(false, UiText.ResourceString(R.string.error_email_not_valid))
        return ValidationResult(true)
    }

}