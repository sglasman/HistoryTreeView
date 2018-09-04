package com.saulglasman.canvastest

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.MotionEvents.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.core.IsAnything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class MainActivityTest {
    @Rule
    @JvmField
    val mainActivityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun treeViewVisibilityTest() {
        Espresso.onView(withId(ID_TREEBUTTON)).perform(click())
        Espresso.onView(withId(ID_TREEVIEW)).check(matches(isDisplayed()))
        Espresso.onView(withId(ID_TREEBUTTON)).perform(click())
        Espresso.onView(withId(ID_TREEVIEW)).check(matches(not(isDisplayed())))
    }

    @Test
    fun undoRedoStackTest() {
        Espresso.onView(withId(ID_EDITBUTTON)).perform(click())
        Espresso.onView(withId(ID_MAINVIEW)).perform(StrokeViewAction(0.2f, 0.2f, 0.8f, 0.8f))
        Espresso.onView(withId(ID_UNDOBUTTON)).check(matches(isEnabled()))
        Espresso.onView(withId(ID_REDOBUTTON)).check(matches(not(isEnabled()))) // after drawing once, undo is available and redo isn't
        Espresso.onView(withId(ID_MAINVIEW)).perform(StrokeViewAction(0.8f, 0.2f, 0.2f, 0.8f))
        Espresso.onView(withId(ID_UNDOBUTTON)).perform(click()).check(matches(isEnabled()))
        Espresso.onView(withId(ID_REDOBUTTON)).check(matches(isEnabled())) // after drawing twice and undoing once, both undo and redo are available
    }

    inner class StrokeViewAction(val x1: Float, val y1: Float, val x2: Float, val y2: Float) : ViewAction {
        override fun getDescription(): String =
                "Execute a stroke from (x1, y1) to (x2, y2), in units of the dimensions of the view"

        override fun getConstraints(): Matcher<View> = IsAnything()

        override fun perform(uiController: UiController?, view: View?) {
            val viewLoc = IntArray(2)
            view!!.getLocationOnScreen(viewLoc)
            val downEvent: MotionEvents.DownResultHolder = sendDown(uiController,
                    floatArrayOf(viewLoc[0].toFloat() + x1 * view.width, viewLoc[1].toFloat() + y1 * view.height),
                    floatArrayOf(1f, 1f))
            sendMovement(uiController, downEvent.down,
                    floatArrayOf(viewLoc[0].toFloat() + x2 * view.width, viewLoc[1].toFloat() + y2 * view.height))
            sendUp(uiController, downEvent.down)
        }
    }
}