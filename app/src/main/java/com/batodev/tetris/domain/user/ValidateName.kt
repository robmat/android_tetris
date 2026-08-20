package com.batodev.tetris.domain.user

import com.batodev.tetris.R
import com.batodev.tetris.presentation.common.UiText

class ValidateName(
    private val name: String,
) : Validator {
    companion object {
        private const val MAX_LENGTH = 20
    }

    override fun execute(): ValidationResult =
        when {
            name.isBlank() -> {
                ValidationResult(false, UiText.ResourceString(R.string.error_name_is_empty))
            }

            name.contains(" ") -> {
                ValidationResult(false, UiText.ResourceString(R.string.error_name_contain_spaces))
            }

            name.contains("\n") -> {
                ValidationResult(false, UiText.ResourceString(R.string.error_name_has_to_be_one_line))
            }

            name.length > MAX_LENGTH -> {
                ValidationResult(false, UiText.ResourceString(R.string.error_name_length_is_out_of_bounds))
            }

            else -> {
                ValidationResult(true)
            }
        }
}
