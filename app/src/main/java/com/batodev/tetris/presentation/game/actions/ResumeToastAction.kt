package com.batodev.tetris.presentation.game.actions

import android.content.Context
import android.widget.Toast
import com.batodev.tetris.R

class ResumeToastAction(private val context: Context) : Action {

    override fun execute() {
        Toast.makeText(context, R.string.resumingin, Toast.LENGTH_LONG).show()
    }

}