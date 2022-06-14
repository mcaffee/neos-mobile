package com.ictis.neos.acceptance

import com.ictis.neos.MainActivity
import com.ictis.neos.R
import com.ictis.neos.framework.AcceptanceTest

import org.junit.Test

class MainActivityAcceptanceTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    @Test
    fun shouldHaveTitle() {
        checkThat.viewIsVisibleAndContainsText("PdMP")
    }

    @Test
    fun shouldOpenDrawer() {
        events.openDrawer()
        checkThat.viewIsVisibleAndContainsText("Здравствуйте,")
    }

    @Test
    fun shouldDrawerHaveMenu() {
        events.openDrawer()
        checkThat.viewIsVisibleAndContainsText("Фильмы")
        checkThat.viewIsVisibleAndContainsText("Избранное")
        checkThat.viewIsVisibleAndContainsText("Об Авторе")
    }

    @Test
    fun shouldDrawerMoviesOpenMovies() {
        events.openDrawer()
        events.clickDrawerItem(R.id.nav_recognition)
    }

    @Test
    fun shouldDrawerMoviesOpenFavorites() {
        events.openDrawer()
        events.clickDrawerItem(R.id.nav_training)
    }

    @Test
    fun shouldDrawerMoviesOpenAbout() {
        events.openDrawer()
        events.clickDrawerItem(R.id.nav_settings)
    }

}