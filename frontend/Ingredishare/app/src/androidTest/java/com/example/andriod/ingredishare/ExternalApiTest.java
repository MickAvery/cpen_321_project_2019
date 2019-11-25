package com.example.andriod.ingredishare;

import com.example.andriod.ingredishare.main.MainActivity;
import com.facebook.FacebookActivity;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExternalApiTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testFacebookActivityLaunched() {
        Intents.init();
        onView(withId(R.id.fb_login_button)).perform(click());
        intended(hasComponent(FacebookActivity.class.getName()), times(1));
        UiDevice.getInstance(getInstrumentation()).pressBack();
        Intents.release();
    }

    // Has to be the last test or it will break
    @Test
    public void testGoogleActivityLaunched() {
        Intents.init();
        onView(withId(R.id.google_sign_in_button)).perform(click());
        intended(hasPackage("com.google.android.gms"));
        UiDevice.getInstance(getInstrumentation()).pressBack();
        UiDevice.getInstance(getInstrumentation()).pressBack();
        Intents.release();
    }
}
