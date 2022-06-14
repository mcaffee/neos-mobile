package com.ictis.neos.acceptance

import com.ictis.neos.LoginActivity
import com.ictis.neos.MainActivity
import com.ictis.neos.R
import com.ictis.neos.framework.AcceptanceTest

import org.junit.Test

class LoginActivityAcceptanceTest : AcceptanceTest<LoginActivity>(LoginActivity::class.java) {

    @Test
    fun shouldHaveLoginButton() {
        checkThat.viewIsVisibleAndContainsText("Login")
    }

    @Test
    fun shouldOpenMainActivityOnCorrectLogin() {
        events.typeText(R.id.edittext_username, "user1")
        events.typeText(R.id.edittext_password, "111")

        events.closeSoftKeyboard(R.id.button_login)

        events.clickOnView(R.id.button_login)

        checkThat.nextOpenActivityIs(MainActivity::class.java)
    }

    @Test
    fun shouldDisplayPopupOnWrongLogin() {
        events.typeText(R.id.edittext_username, "user1")
        events.typeText(R.id.edittext_password, "222")

        events.closeSoftKeyboard(R.id.button_login)

        events.clickOnView(R.id.button_login)

        checkThat.toastIsDisplayed("Wrong password!")
    }

}