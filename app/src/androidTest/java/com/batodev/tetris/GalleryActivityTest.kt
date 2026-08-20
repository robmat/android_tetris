package com.batodev.tetris

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.gallery.GalleryActivity
import com.batodev.tetris.presentation.gallery.IMAGES
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// GalleryActivity is a plain Activity (not even AppCompatActivity), launched
// directly here via an IMAGES string-array extra rather than through real
// gameplay - reaching a real win naturally would require actually scoring
// 200+/450+/700+ points across three separate score tiers in a live Tetris
// game, impractical to simulate reliably via Espresso (see GameActivityTest
// for why simulating a full game isn't attempted at all in this app).
//
// onCreate() resumes from SettingsHelper's persisted lastSeenGalleryImageIndex
// (a real, intentional "continue where you left off" feature) rather than
// always starting at 0 - settings must be reset every test or state left
// over from an earlier test (or an earlier run of this same test) bleeds
// into the initial index/button-visibility assertions.
@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {
    private lateinit var images: Array<String>

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.filesDir.resolve(SettingsHelper::class.java.simpleName).delete()
        images =
            context.assets
                .list("pics/tier1")!!
                .take(3)
                .toTypedArray()
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    private fun launchWithImages(): ActivityScenario<GalleryActivity> {
        val intent =
            Intent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                GalleryActivity::class.java,
            ).apply { putExtra(IMAGES, images) }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun launchesShowingFirstImageWithLeftHiddenAndRightVisible() {
        val scenario = launchWithImages()

        onView(withId(R.id.photoView)).check(matches(isDisplayed()))
        onView(withId(R.id.gallery_left)).check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.gallery_right)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun leftClickAtFirstImageNoOpsWithoutCrashing() {
        val scenario = launchWithImages()

        onView(withId(R.id.gallery_left)).check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.photoView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun rightClickToLastImageHidesRightAndShowsLeft() {
        val scenario = launchWithImages()

        onView(withId(R.id.gallery_right)).perform(click())
        onView(withId(R.id.gallery_right)).perform(click())

        onView(withId(R.id.gallery_right)).check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.gallery_left)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun shareButtonSharesCurrentImage() {
        val scenario = launchWithImages()

        onView(withId(R.id.gallery_share)).perform(click())

        intended(hasAction(Intent.ACTION_SEND))
        intended(hasType("image/*"))
        scenario.close()
    }

    @Test
    fun backButtonFinishesActivity() {
        val scenario = launchWithImages()

        onView(withId(R.id.gallery_back_btn)).perform(click())

        assertEventuallyDestroyed(scenario)
    }

    @Test
    fun systemBackPressFinishesActivity() {
        val scenario = launchWithImages()

        assertBackPressFinishesScenario(scenario)
    }
}
