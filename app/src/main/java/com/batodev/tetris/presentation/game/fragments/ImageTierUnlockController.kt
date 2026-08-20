package com.batodev.tetris.presentation.game.fragments

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.batodev.tetris.R
import com.batodev.tetris.infra.images.ImageHelper
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.settings.SettingsSingleton
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.xml.listeners.OnParticleSystemUpdateListener
import java.util.concurrent.TimeUnit

/**
 * Checks score thresholds to unlock the next gallery image (with a confetti +
 * snackbar celebration), split out of [GameFragment] since this was the bulk
 * of its function count.
 */
class ImageTierUnlockController(
    private val fragment: GameFragment,
) {
    companion object {
        private const val TIER_ONE_SCORE_REQUIRED = 200
        private const val TIER_TWO_SCORE_REQUIRED = 450
        private const val TIER_THREE_SCORE_REQUIRED = 700
        private const val IMAGE_TIER_TWO = 2
        private const val IMAGE_TIER_THREE = 3
        private const val CONFETTI_MAX_SPEED = 30f
        private const val CONFETTI_DAMPING = 0.9f
        private const val CONFETTI_SPREAD_DEGREES = 360
        private const val CONFETTI_COLOR_1 = 0xfce18a
        private const val CONFETTI_COLOR_2 = 0xff726d
        private const val CONFETTI_COLOR_3 = 0xf4306d
        private const val CONFETTI_COLOR_4 = 0xb48def
        private const val CONFETTI_EMITTER_DURATION_MS = 100L
        private const val CONFETTI_EMITTER_MAX_PARTICLES = 100
        private const val CONFETTI_POSITION_X = 0.5
        private const val CONFETTI_POSITION_Y = 0.3
    }

    private var tierOneImageUncovered = false
    private var tierTwoImageUncovered = false
    private var tierThreeImageUncovered = false

    fun checkIfImageIsWon() {
        val score =
            fragment.model.gameFacade.value!!
                .getScore()
                .value
        Log.d(GameFragment::class.java.simpleName, "Score: $score")
        if (score >= TIER_ONE_SCORE_REQUIRED && !tierOneImageUncovered) {
            tierOneImageUncovered = true
            addImageToUncoveredAndPickNew(IMAGE_TIER_TWO)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
        if (score >= TIER_TWO_SCORE_REQUIRED && !tierTwoImageUncovered) {
            tierTwoImageUncovered = true
            addImageToUncoveredAndPickNew(IMAGE_TIER_THREE)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
        if (score >= TIER_THREE_SCORE_REQUIRED && !tierThreeImageUncovered) {
            tierThreeImageUncovered = true
            addImageToUncoveredAndPickNew(Integer.MAX_VALUE)
            showTopSnackBar()
            showConfettiAndPlaySound()
        }
    }

    private fun showConfettiAndPlaySound() {
        val settingsData = SettingsSingleton.getSettingsData(fragment.requireContext())
        if (settingsData.hasSounds) {
            fragment.imagePlayer.start()
        }
        val party =
            Party(
                speed = 0f,
                maxSpeed = CONFETTI_MAX_SPEED,
                damping = CONFETTI_DAMPING,
                spread = CONFETTI_SPREAD_DEGREES,
                colors = listOf(CONFETTI_COLOR_1, CONFETTI_COLOR_2, CONFETTI_COLOR_3, CONFETTI_COLOR_4),
                emitter =
                    Emitter(duration = CONFETTI_EMITTER_DURATION_MS, TimeUnit.MILLISECONDS)
                        .max(CONFETTI_EMITTER_MAX_PARTICLES),
                position = Position.Relative(CONFETTI_POSITION_X, CONFETTI_POSITION_Y),
            )
        val konfetti = fragment.requireView().findViewById<KonfettiView>(R.id.konfettiView)
        konfetti.visibility = View.VISIBLE
        konfetti.start(party)
        konfetti.onParticleSystemUpdateListener =
            object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(
                    view: KonfettiView,
                    party: Party,
                    activeSystems: Int,
                ) {
                    konfetti.visibility = View.GONE
                    Log.d(GameFragment::class.java.simpleName, "confetti end: $konfetti")
                }

                override fun onParticleSystemStarted(
                    view: KonfettiView,
                    party: Party,
                    activeSystems: Int,
                ) {
                    Log.d(GameFragment::class.java.simpleName, "confetti start: $konfetti")
                }
            }
    }

    private fun showTopSnackBar() {
        val snackBar =
            Snackbar.make(fragment.view, fragment.getString(R.string.newImageInGallery), Snackbar.LENGTH_SHORT)
        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackBar.view.layoutParams = params
        snackBar.show()
    }

    private fun addImageToUncoveredAndPickNew(newImageTier: Int) {
        Log.d(GameFragment::class.java.simpleName, "image won newImageTier: $newImageTier")
        Log.d(
            GameFragment::class.java.simpleName,
            "image won imageData.fileName: ${fragment.imageData.fileName}",
        )
        val settingsData = SettingsHelper.load(fragment.requireActivity())
        val imagesWon = settingsData.imagesWon
        if (!imagesWon.contains(fragment.imageData.fileName)) {
            imagesWon.add(fragment.imageData.fileName)
            SettingsHelper.save(fragment.requireActivity(), settingsData)
        }
        if (!fragment.imagesWonThisGame.contains(fragment.imageData.fileName)) {
            fragment.imagesWonThisGame.add(fragment.imageData.fileName)
        }
        if (newImageTier == IMAGE_TIER_TWO) {
            fragment.imageData = ImageHelper.pickTierTwoImage(fragment.requireActivity())
        }
        if (newImageTier == IMAGE_TIER_THREE) {
            fragment.imageData = ImageHelper.pickTierThreeImage(fragment.requireActivity())
        }
        fragment.requireView().findViewById<ImageView>(R.id.GameImage).setImageBitmap(fragment.imageData.bitmap)
        Log.d(GameFragment::class.java.simpleName, "new image: ${fragment.imageData.fileName}")
    }
}
