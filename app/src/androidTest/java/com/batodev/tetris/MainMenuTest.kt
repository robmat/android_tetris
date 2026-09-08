package com.batodev.tetris

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
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
    private var splashIdlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.filesDir.resolve(SettingsHelper::class.java.simpleName).delete()
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
        splashIdlingResource?.let { IdlingRegistry.getInstance().unregister(it) }
        splashIdlingResource = null
    }

    // The splash screen's setKeepOnScreenCondition tracks MainModel.isLoading, which only
    // flips to false after a plain real-time delay() (see ViewModel.kt) - not something
    // Espresso already knows to wait for. Without this, click() can land while the splash
    // overlay still covers the menu, swallowing the tap (observed flakily on
    // moreAppsButtonOpensDeveloperPlayStorePage).
    private fun launchMainActivityAndWaitForSplash(): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val resource =
            object : IdlingResource {
                @Volatile private var callback: IdlingResource.ResourceCallback? = null

                @Volatile private var idle = false

                override fun getName() = "MainActivitySplashIdlingResource"

                override fun isIdleNow(): Boolean {
                    if (!idle) {
                        scenario.onActivity { activity ->
                            if (!activity.viewModel.isLoading.value) {
                                idle = true
                                callback?.onTransitionToIdle()
                            }
                        }
                    }
                    return idle
                }

                override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback?) {
                    callback = cb
                }
            }
        splashIdlingResource = resource
        IdlingRegistry.getInstance().register(resource)
        return scenario
    }

    @Test
    fun playButtonOpensGameActivity() {
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_play_the_game)).perform(click())

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonShowsSnackbarWithNoImagesWon() {
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_unlocked_gallery)).perform(click())

        onView(withText_playTheGameToUnlockImages()).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonOpensGalleryActivityWithImagesWon() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        seedImagesWon(context.assets.list("pics/tier1")!!.first())
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_unlocked_gallery)).perform(click())

        onView(withId(R.id.photoView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun settingsButtonOpensSettingsActivity() {
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_settings)).perform(click())

        onView(withId(R.id.settings_activity_back)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun moreAppsButtonOpensDeveloperPlayStorePage() {
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_more_apps)).perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse("https://play.google.com/store/apps/dev?id=8228670503574649511")))
        scenario.close()
    }

    @Test
    fun rateButtonShowsPopupAndLaterDismissesIt() {
        val scenario = launchMainActivityAndWaitForSplash()

        onView(withId(R.id.main_menu_activity_rate)).perform(click())

        onView(withId(R.id.btnRateLater)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        onView(withId(R.id.btnRateLater)).inRoot(isPlatformPopup()).perform(click())

        onView(withId(R.id.main_menu_activity_play_the_game)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun pressingBackShowsQuitDialogAndCancelStaysOnMainMenu() {
        val scenario = launchMainActivityAndWaitForSplash()

        assertQuitDialogCancelable(scenario)

        onView(withId(R.id.main_menu_activity_play_the_game)).check(matches(isDisplayed()))
        scenario.close()
    }
}

private fun withText_playTheGameToUnlockImages() =
    androidx.test.espresso.matcher.ViewMatchers
        .withText(R.string.playTheGameTounlockImages)
