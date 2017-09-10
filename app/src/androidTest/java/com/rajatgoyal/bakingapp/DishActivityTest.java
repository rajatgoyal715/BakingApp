package com.rajatgoyal.bakingapp;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.util.DisplayMetrics;

import com.rajatgoyal.bakingapp.ui.DishActivity;
import com.rajatgoyal.bakingapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by rajat on 6/9/17.
 */

public class DishActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Activity mActivity;

    @Before
    public void setup() {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void clickIngredientsItemOpensIngredientsActivity() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.dishes_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.ingredientsLabel)).perform(click());
        onView(withId(R.id.ingredients_list)).check(matches(withId(R.id.ingredients_list)));

        pressBack();

        checkIfStepItemOneCorrectlyDisplayed();
    }

    private void checkIfStepItemOneCorrectlyDisplayed() {
        onView(withId(R.id.steps_list)).perform(actionOnItemAtPosition(0, click()));
        onView(allOf(withId(R.id.descripton_text_view), withText("Recipe Introduction"))).check(matches(isDisplayed()));

        // if user is on phone, then next and previous buttons are checked
        if (!isScreenSw600dp()) {
            checkNextAndPreviousButtons();
        }
    }

    private void checkNextAndPreviousButtons() {

        // click next button
        onView(withId(R.id.next_button)).perform(click());

        // click previous button
        onView(withId(R.id.previous_button)).perform(click());

        //click on pause button of exo player
        onView(withId(R.id.exo_pause)).perform(click());
    }

    // check if screen is of phone or tablet
    private boolean isScreenSw600dp() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / displayMetrics.density;
        float screenSw = Math.min(widthDp, heightDp);
        return screenSw >= 600;
    }
}
