package com.batodev.tetris

import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.batodev.tetris.infra.settings.SettingsData
import com.batodev.tetris.infra.settings.SettingsHelper
import com.batodev.tetris.presentation.main.MainActivity
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.sameInstance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals

// Shared across MainMenuTest/SettingsActivityTest/GameActivityTest/FinishedActivityTest/
// GalleryActivityTest.
//
// IMPORTANT: MainActivity, GameActivity, and FinishedActivity all extend
// HideStatusBarActivity, which registers an OnBackPressedCallback to show a
// "Quit app?" AlertDialog instead of just finishing - confirming it calls
// finishAffinity() + exitProcess(0), which would kill this entire
// instrumentation process, not just the Activity under test. The usual
// press-back-and-expect-DESTROYED pattern used elsewhere in this workspace is
// therefore unsafe here - assertQuitDialogCancelable() below verifies the
// dialog appears and dismisses it via Cancel (never OK) instead.
//
// Real bug found and fixed in app code: HideStatusBarActivity originally
// called super.onBackPressed() before showing the dialog, which (on a
// root-task Activity, as all three of these are) finishes the Activity via
// the legacy callback path immediately - the dialog was dead code, so a real
// back press just silently exited with no confirmation ever shown, on any
// device where the platform's predictive-back callback path is active
// (default here, since targetSdk 37 defaults android:enableOnBackInvokedCallback
// to true). Fixed by registering a proper OnBackPressedCallback instead.
//
// assertQuitDialogCancelable() below triggers that callback directly via
// activity.onBackPressedDispatcher.onBackPressed() rather than Espresso's
// pressBack() (which injects a real KEYCODE_BACK) - confirmed via logcat (a
// WindowLeaked warning naming MainActivity's dialog DecorView) that the
// dialog genuinely was being shown correctly either way, but Espresso's
// default root picker (which requires the picked root to hold real window
// focus) never managed to settle on the dialog's window in this Activity -
// consistently timing out after 10s still reporting the *Activity's own*
// (unfocused) window, never the dialog, regardless of adding warm-up
// onView() calls beforehand to let the Activity's window settle first.
// Root-caused to HideStatusBarActivity's aggressive edge-to-edge/hidden
// system-bars window flags somehow preventing the dialog window from being
// recognized as focused by Espresso's picker on this device - worked around
// by matching the dialog's root structurally instead of by focus, via
// inRoot(withDecorView(not(sameInstance(activity.window.decorView)))), the
// standard Espresso recipe for targeting "whichever root isn't the Activity's
// own" - this is what actually made the check reliable.
// SettingsActivity and GalleryActivity are plain/AppCompatActivity with no
// such callback registered, so the standard pattern is used for those instead.

fun assertQuitDialogCancelable(scenario: ActivityScenario<out ComponentActivity>) {
    onView(isRoot()).check(matches(isDisplayed()))
    var activityDecorView: View? = null
    scenario.onActivity { activity ->
        activityDecorView = activity.window.decorView
        activity.onBackPressedDispatcher.onBackPressed()
    }
    val dialogRoot = withDecorView(not(sameInstance(activityDecorView)))
    onView(withText(R.string.quitAppMessage)).inRoot(dialogRoot).check(matches(isDisplayed()))
    onView(withText(R.string.cancel)).inRoot(dialogRoot).perform(click())
    assertNotEquals(Lifecycle.State.DESTROYED, scenario.state)
}

fun assertEventuallyDestroyed(
    scenario: ActivityScenario<*>,
    timeoutMs: Long = 8_000,
) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (scenario.state != Lifecycle.State.DESTROYED && System.currentTimeMillis() < deadline) {
        Thread.sleep(50)
    }
    assertEquals(Lifecycle.State.DESTROYED, scenario.state)
}

/**
 * For plain Activities (SettingsActivity, GalleryActivity - neither extends
 * HideStatusBarActivity, so neither has a quit-confirmation callback
 * intercepting back navigation) launched standalone via ActivityScenario:
 * each is the sole Activity in its own synthetic task, so a real back press
 * exhausts the task and Espresso reports it via NoActivityResumedException
 * rather than returning normally.
 */
fun assertBackPressFinishesScenario(scenario: ActivityScenario<*>) {
    try {
        pressBack()
    } catch (expected: NoActivityResumedException) {
    }
    assertEventuallyDestroyed(scenario)
}

/**
 * SettingsHelper.load()/save() round-trip through a JSON file per call
 * (Gson), not a live in-memory singleton like the other apps in this
 * workspace - there's no shared object to just mutate in place, so
 * persisting a real settings file (via a real, if short-lived, Activity to
 * get a filesDir-bearing Context) is the only way to seed state that a
 * later Activity's own load() will actually pick up.
 */
fun seedImagesWon(vararg pics: String) {
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    scenario.onActivity { activity ->
        SettingsHelper.save(activity, SettingsData(imagesWon = pics.toMutableList()))
    }
    scenario.close()
}
