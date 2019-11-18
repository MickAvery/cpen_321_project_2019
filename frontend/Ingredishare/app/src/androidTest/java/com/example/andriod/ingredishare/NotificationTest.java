package com.example.andriod.ingredishare;

import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.facebook.FacebookActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationTest {

    @Rule
    public ActivityTestRule<NewIngrediPostActivity> activityRule
            = new ActivityTestRule<>(NewIngrediPostActivity.class);

    @Test
    public void testPostWorks() {
        onView(withId(R.id.name)).perform(typeText("name"), closeSoftKeyboard());
        onView(withId(R.id.description)).perform(typeText("description"), closeSoftKeyboard());
        onView(withId(R.id.postbutton)).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
        UiDevice.getInstance(getInstrumentation()).pressBack();
    }

    @Test
    public void testPostWorksWithEmptyName() {
        onView(withId(R.id.description)).perform(typeText("description"), closeSoftKeyboard());
        onView(withId(R.id.postbutton)).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
        UiDevice.getInstance(getInstrumentation()).pressBack();
    }

    @Test
    public void testPostWorksWithEmptyDescription() {
        onView(withId(R.id.name)).perform(typeText("name"), closeSoftKeyboard());
        onView(withId(R.id.postbutton)).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
        UiDevice.getInstance(getInstrumentation()).pressBack();
    }
}
