package com.rajatgoyal.bakingapp;

import android.support.test.rule.ActivityTestRule;

import com.rajatgoyal.bakingapp.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by rajat on 5/9/17.
 */

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecyclerViewItemOpensActivity() throws InterruptedException {
        Thread.sleep(3000);

        onView(withId(R.id.dishes_list))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.steps_list)).check(matches(withId(R.id.steps_list)));

    }
}
