package com.saulglasman.canvastest

import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class MainActivityTest {
    @Rule @JvmField
    val activity = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun treeViewVisibilityTest() {
        Espresso.onView(withId(ID_TREEBUTTON)).perform(click())
        Espresso.onView(withId(ID_TREEVIEW)).check(matches(isDisplayed()))
        Espresso.onView(withId(ID_TREEBUTTON)).perform(click())
        Espresso.onView(withId(ID_TREEVIEW)).check(matches(not(isDisplayed())))

    }
}