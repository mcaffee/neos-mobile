package com.ictis.neos.framework

import android.app.Activity
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*


class Matchers {


    fun <T : Activity> nextOpenActivityIs(clazz: Class<T>) {
        intended(IntentMatchers.hasComponent(clazz.name))
    }

    fun viewIsVisibleAndContainsText(@StringRes stringResource: Int) {
        onView(withText(stringResource)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    fun viewIsVisibleAndContainsText(@StringRes stringResource: String) {
        onView(withText(stringResource)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    fun viewContainsText(@IdRes viewId: Int, @StringRes stringResource: Int) {
        onView(withId(viewId)).check(matches(withText(stringResource)))
    }

    fun viewContainsText(@IdRes viewId: Int, @StringRes stringResource: String) {
        onView(withId(viewId)).check(matches(withText(stringResource)))
    }

    fun toastIsDisplayed(@StringRes stringResource: String) {
        // onView(withText(stringResource)).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }
}