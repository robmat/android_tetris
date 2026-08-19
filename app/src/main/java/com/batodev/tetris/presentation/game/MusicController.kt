package com.batodev.tetris.presentation.game

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.batodev.tetris.R

/**
 * Owns the background music [MediaPlayer] and its paused-position bookkeeping -
 * split out of [GameViewModel].
 */
class MusicController {
    private val lengthSong: MutableLiveData<Int> = MutableLiveData(0)
    private val song: MutableLiveData<MediaPlayer> = MutableLiveData(null)

    fun setUp(hasMusic: Boolean, context: Context) {
        if (hasMusic) {
            song.value = MediaPlayer.create(context, R.raw.tetristheme)
            song.value?.isLooping = true
        }
    }

    fun pause() {
        if (song.value != null && song.value!!.isPlaying) {
            song.value?.pause()
            lengthSong.value = song.value?.currentPosition
        }
    }

    fun start() {
        if (song.value != null) {
            song.value?.seekTo(lengthSong.value!!)
            song.value?.start()
        }
    }
}
