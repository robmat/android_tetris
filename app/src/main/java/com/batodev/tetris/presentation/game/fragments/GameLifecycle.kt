package com.batodev.tetris.presentation.game.fragments

import androidx.lifecycle.lifecycleScope
import com.batodev.tetris.R
import com.batodev.tetris.presentation.game.PlayPauseView
import com.batodev.tetris.presentation.game.State
import com.batodev.tetris.presentation.game.actions.Action
import com.batodev.tetris.presentation.game.actions.ResumeToastAction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

/**
 * Owns starting/pausing/resuming the falling-block coroutine and background
 * music together with the pause button's state, split out of [GameFragment]
 * since this was the bulk of its function count.
 */
class GameLifecycle(private val fragment: GameFragment) {
    private val resumeAction: Action = ResumeToastAction(fragment.requireContext())

    fun startGame() {
        fragment.model.music.start()
        fragment.moveBlockDown = fragment.lifecycleScope.launch(start = CoroutineStart.ATOMIC) {
            fragment.model.runGame()
        }
        fragment.requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PLAY)
        fragment.requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
    }

    fun pauseGame() {
        if (!fragment.model.state.isGamePaused()) {
            fragment.moveBlockDown.cancel()
            fragment.model.music.pause()
            fragment.model.state.setGamePaused()
            fragment.requireView().findViewById<PlayPauseView>(R.id.pauseButton).setState(State.PAUSE)
            fragment.requireView().findViewById<PlayPauseView>(R.id.pauseButton).fadeIn()
        }
    }

    fun pauseButtonClicked() {
        if (fragment.model.state.isGamePaused()) {
            resumeAction.execute()
            resumeGame()
        } else {
            pauseGame()
        }
    }

    private fun resumeGame() {
        if (fragment.model.state.isGamePaused()) {
            startGame()
            fragment.model.state.setGameResume()
        }
    }
}
