package com.batodev.tetris

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.batodev.tetris.presentation.game.GameActivity
import org.junit.Test
import org.junit.runner.RunWith

// Real gameplay needs 200+ points to unlock even the first image tier
// (GameFragment.tierOneScoreRequired), and losing needs the grid to fill up
// entirely - neither is practical to simulate reliably via Espresso, so this
// only verifies the controls themselves respond safely, not a full game
// reaching FinishedActivity (covered separately in FinishedActivityTest via
// a direct Intent launch).
@RunWith(AndroidJUnit4::class)
class GameActivityTest {

    @Test
    fun launchesShowingGridAndControls() {
        val scenario = ActivityScenario.launch(GameActivity::class.java)

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        onView(withId(R.id.GameImage)).check(matches(isDisplayed()))
        onView(withId(R.id.PointsText)).check(matches(isDisplayed()))
        onView(withId(R.id.NextBlockImage)).check(matches(isDisplayed()))
        onView(withId(R.id.pauseButton)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun moveAndRotateButtonsRespondWithoutCrashing() {
        val scenario = ActivityScenario.launch(GameActivity::class.java)

        onView(withId(R.id.LeftButton)).perform(click())
        onView(withId(R.id.RightButton)).perform(click())
        onView(withId(R.id.RotateLeft)).perform(click())
        onView(withId(R.id.RotateRight)).perform(click())
        onView(withId(R.id.DownButton)).perform(click())

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun pauseButtonTogglesPauseAndResumeWithoutCrashing() {
        val scenario = ActivityScenario.launch(GameActivity::class.java)

        onView(withId(R.id.pauseButton)).perform(click())
        onView(withId(R.id.pauseButton)).perform(click())

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun pressingBackShowsQuitDialogAndCancelStaysInGame() {
        val scenario = ActivityScenario.launch(GameActivity::class.java)

        assertQuitDialogCancelable(scenario)

        onView(withId(R.id.GameGrid)).check(matches(isDisplayed()))
        scenario.close()
    }
}
