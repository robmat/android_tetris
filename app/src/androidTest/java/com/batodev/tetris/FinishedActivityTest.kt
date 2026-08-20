package com.batodev.tetris

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.batodev.tetris.infra.settings.SettingsData
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.finished.FinishedActivity
import com.batodev.tetris.presentation.game.fragments.IMAGES_WON_THIS_GAME
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// FinishedActivity is normally reached after a real game ends (needs 200+
// points just for the first image tier - impractical to simulate reliably
// via Espresso), so it's launched directly here via the same
// IMAGES_WON_THIS_GAME intent extra GameFragment.finishGame() passes in
// real play, using real assets/pics/tier1 filenames so ImageHelper's real
// bitmap-decoding path is exercised rather than skipped.
@RunWith(AndroidJUnit4::class)
class FinishedActivityTest {
    private lateinit var wonImages: Array<String>

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        wonImages =
            context.assets
                .list("pics/tier1")!!
                .take(2)
                .toTypedArray()
        context.filesDir.resolve(SettingsHelper::class.java.simpleName).delete()
    }

    private fun launchWithWonImages(): ActivityScenario<FinishedActivity> {
        val intent =
            Intent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                FinishedActivity::class.java,
            ).apply { putExtra(IMAGES_WON_THIS_GAME, wonImages) }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun launchesShowingWonImages() {
        val scenario = launchWithWonImages()

        onView(withId(R.id.won_image_1)).check(matches(isDisplayed()))
        onView(withId(R.id.won_image_2)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun playAgainButtonStartsNewGame() {
        val scenario = launchWithWonImages()

        onView(withId(R.id.finished_activity_play_the_game)).perform(click())

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonOpensGalleryWithPersistedWonImages() {
        seedImagesWon(*wonImages)
        val scenario = launchWithWonImages()

        onView(withId(R.id.finished_activity_unlocked_gallery)).perform(click())

        onView(withId(R.id.photoView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun pressingBackShowsQuitDialogAndCancelStaysOnFinishedActivity() {
        val scenario = launchWithWonImages()

        assertQuitDialogCancelable(scenario)

        onView(withId(R.id.finished_activity_play_the_game)).check(matches(isDisplayed()))
        scenario.close()
    }
}
