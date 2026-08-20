package com.batodev.tetris.presentation.common

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class PrimitiveString(
        val value: String,
    ) : UiText()

    class ResourceString(
        @StringRes val resourceId: Int,
        vararg val args: Any,
    ) : UiText()

    fun asString(context: Context): String =
        when (this) {
            is PrimitiveString -> {
                value
            }

            is ResourceString -> {
                val newArgs = this.args.map { if (it is UiText) it.asString(context) else it }.toTypedArray()
                // Context.getString's formatArgs is a Java vararg; Kotlin has no way to forward a
                // runtime-sized array into a vararg call except the spread operator.
                @Suppress("SpreadOperator")
                context.getString(resourceId, *newArgs)
            }
        }
}
