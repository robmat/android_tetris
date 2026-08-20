package com.batodev.tetris.presentation.common

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.batodev.tetris.R
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.gallery.GalleryActivity
import com.batodev.tetris.presentation.gallery.IMAGES
import com.google.android.material.snackbar.Snackbar

/**
 * Navigates to the gallery of unlocked (won) images, or, if none have been
 * won yet, shows a snackbar prompting the user to play the game first.
 */
fun AppCompatActivity.openUnlockedGalleryOrPromptToPlay(rootView: View) {
    val imagesWon = SettingsHelper.load(this).imagesWon
    if (imagesWon.isNotEmpty()) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.putExtra(IMAGES, imagesWon.toTypedArray())
        startActivity(intent)
    } else {
        showUnlockGalleryPrompt(rootView)
    }
}

private fun AppCompatActivity.showUnlockGalleryPrompt(rootView: View) {
    val snackBar = Snackbar.make(
        rootView,
        getString(R.string.playTheGameTounlockImages),
        Snackbar.LENGTH_SHORT
    )
    val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    snackBar.view.layoutParams = params
    snackBar.show()
}
