package com.hzjytech.coffeeme.home;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by hehongcan on 2017/10/19.
 */
public class ModulationActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule=new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testDisplay() throws Exception {
        onView(withId(R.id.rv_home_drink)).perform(
                RecyclerViewActions.actionOnItemAtPosition(4, ViewActions.click()));
        // Then I'd like to check that the tab text (test2) matches the current fragment title

    }
}