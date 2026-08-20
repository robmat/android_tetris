package com.batodev.tetris

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Covers every button on activity_main.xml: play, gallery (both the
// empty-state Snackbar and the real-navigation case), settings, more-apps,
// rate (dismiss via Later), and the custom quit-confirmation back-press
// (MainActivity extends HideStatusBarActivity - see EspressoTestSupport.kt).
@RunWith(AndroidJUnit4::class)
class MainMenuTest {
    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.filesDir.resolve(SettingsHelper::class.java.simpleName).delete()
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun playButtonOpensGameActivity() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_play_the_game)).perform(click())

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonShowsSnackbarWithNoImagesWon() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_unlocked_gallery)).perform(click())

        onView(withText_playTheGameToUnlockImages()).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonOpensGalleryActivityWithImagesWon() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        seedImagesWon(context.assets.list("pics/tier1")!!.first())
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_unlocked_gallery)).perform(click())

        onView(withId(R.id.photoView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun settingsButtonOpensSettingsActivity() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_settings)).perform(click())

        onView(withId(R.id.settings_activity_back)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun moreAppsButtonOpensDeveloperPlayStorePage() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_more_apps)).perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse("https://play.google.com/store/apps/dev?id=8228670503574649511")))
        scenario.close()
    }

    @Test
    fun rateButtonShowsPopupAndLaterDismissesIt() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.main_menu_activity_rate)).perform(click())

        onView(withId(R.id.btnRateLater)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        onView(withId(R.id.btnRateLater)).inRoot(isPlatformPopup()).perform(click())

        onView(withId(R.id.main_menu_activity_play_the_game)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun pressingBackShowsQuitDialogAndCancelStaysOnMainMenu() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        assertQuitDialogCancelable(scenario)

        onView(withId(R.id.main_menu_activity_play_the_game)).check(matches(isDisplayed()))
        scenario.close()
    }
}

private fun withText_playTheGameToUnlockImages() =
    androidx.test.espresso.matcher.ViewMatchers
        .withText(R.string.playTheGameTounlockImages)
