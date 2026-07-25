package com.batodev.tetris

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.batodev.tetris.presentation.settings.SettingsActivity
import org.junit.Test
import org.junit.runner.RunWith

// SettingsActivity is a plain AppCompatActivity (does not extend
// HideStatusBarActivity), so standard back-press behavior applies - no
// quit-confirmation dialog. Kept minimal: opening it and both ways of
// leaving it (its own back button, the system back gesture).
@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Test
    fun backButtonFinishesActivity() {
        val scenario = ActivityScenario.launch(SettingsActivity::class.java)

        onView(withId(R.id.settings_activity_back)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_activity_back)).perform(click())

        assertEventuallyDestroyed(scenario)
    }

    @Test
    fun systemBackPressFinishesActivity() {
        val scenario = ActivityScenario.launch(SettingsActivity::class.java)

        assertBackPressFinishesScenario(scenario)
    }
}
