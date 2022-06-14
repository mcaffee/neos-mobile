package com.ictis.neos.framework

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import com.ictis.neos.R

class Events {

    fun clickOnView(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(click())
    }

    fun typeText(@IdRes viewId: Int, text: String) {
        onView(withId(viewId)).perform(typeText(text))
    }

    fun pressBack(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(pressBack())
    }

    fun swipeRight(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeRight())
    }

    fun swipeLeft(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(swipeLeft())
    }

    fun closeSoftKeyboard(@IdRes viewId: Int) {
        onView(withId(viewId)).perform(closeSoftKeyboard())
    }

    fun openDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    }

    fun clickDrawerItem(@IdRes viewId: Int) {
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(viewId))
    }


}