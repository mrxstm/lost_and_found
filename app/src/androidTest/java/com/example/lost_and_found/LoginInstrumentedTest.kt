package com.example.lost_and_found

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lost_and_found.view.Dashboard
import com.example.lost_and_found.view.LoginScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginScreen>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun  loginScreen_validCredentials_navigatesToDashboard() {
        // Enter email
        composeRule.onNodeWithTag("email")
            .performTextInput("niranshrestha131@gmail.com")

        // Enter password
        composeRule.onNodeWithTag("password")
            .performTextInput("@Satyam1234")

        // Click login
        composeRule.onNodeWithTag("loginButton")
            .performClick()

        Thread.sleep(5000)


        //  Verify navigation to Dashboard
        Intents.intended(hasComponent(Dashboard::class.java.name))
    }

    @Test
    fun loginScreen_emptyFields_staysOnLoginScreen() {

        composeRule.onNodeWithTag("loginButton")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithTag("loginButton")
            .assertExists()
    }
}