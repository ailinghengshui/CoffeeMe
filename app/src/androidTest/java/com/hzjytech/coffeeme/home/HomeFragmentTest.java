package com.hzjytech.coffeeme.home;

import android.support.test.rule.ActivityTestRule;

import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Hades on 2016/8/30.
 */
public class HomeFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> rule=new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testCompleteDisplay() throws Exception {
        onView(withId(R.id.tbHomefrgTitle)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testDisplay() throws Exception {
        onView(withId(R.id.tbHomefrgTitle)).check(matches(isDisplayed()));

    }
}