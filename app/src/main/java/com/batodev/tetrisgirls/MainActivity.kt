package com.batodev.tetrisgirls

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets.Type
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.button_fast_down
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import kotlinx.android.synthetic.main.activity_main.button_rotate
import kotlinx.android.synthetic.main.activity_main.canvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(Type.statusBars())
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()

        button_rotate.setOnClickListener {
            if (Rotate.isRotable()) {
                Rotate.doRotate()
                canvas.invalidate()
            }
        }

        button_left.setOnClickListener {
            if (MoveLeft.isMovableLeft()) {
                MoveLeft.moveLeft()
                canvas.invalidate()
            }
        }
        button_right.setOnClickListener {
            if (MoveRight.isMovableRight()) {
                MoveRight.moveRight()
                canvas.invalidate()
            }
        }
        button_fast_down.setOnClickListener {
            while (!Falling.willLanding(1)) {
                Falling.fallingStep()
            }
        }
        // run game
        game()
    }

    private fun game() {
        CoroutineScope(Dispatchers.IO).launch {
            // todo eliminate this
            Level.reset()
            Tetromino.newPiece()
            Level.insertNewPosition()
            setBest()

            // gamplay infinite
            while (true) {
                if (Falling.willLanding(1)) {
                    // check is need to clear rows
                    Level.checkRows()
                    // if landed piece cant entered
                    if (Level.isGameOver()) {
                        resetBest()
                        Level.reset()
                    }
                    Tetromino.newPiece()
                    Level.insertNewPosition()
                } else {
                    Falling.fallingStep()
                }
                //game speed in millisecond
                delay(Tetromino.speed)
                canvas.invalidate()
            }
        }
    }

    private fun setBest() {
        val sharedPreference = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE)
        Level.best = sharedPreference.getInt("high_score", 0)
    }

    private fun resetBest() {
        val sharedPreference = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE)
        if (Level.score > sharedPreference.getInt("high_score", 0)) {
            val editor = sharedPreference.edit()
            editor.putInt("high_score", Level.score)
            editor.apply()
            Level.best = sharedPreference.getInt("high_score", 0)
        }
    }
}