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
import com.example.lost_and_found.view.SignupScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignupScreen>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun signupScreen_validDetails_navigatesToDashboard() {
        // Unique email to avoid "already in use" error on repeat runs
        val uniqueEmail = "test${System.currentTimeMillis()}@gmail.com"

        composeRule.onNodeWithTag("fullName")
            .performTextInput("John Doe")

        composeRule.onNodeWithTag("email")
            .performTextInput(uniqueEmail)

        composeRule.onNodeWithTag("password")
            .performTextInput("123456")

        composeRule.onNodeWithTag("termsCheckbox")
            .performScrollTo()
            .performClick()

        // Scroll to button first — it's below the fold in LazyColumn
        composeRule.onNodeWithTag("signupButton")
            .performScrollTo()
            .performClick()

        // Wait for Firebase async registration and navigation
        Thread.sleep(5000)

        Intents.intended(hasComponent(Dashboard::class.java.name))
    }

    @Test
    fun signupScreen_emptyFields_staysOnSignupScreen() {
        composeRule.onNodeWithTag("signupButton")
            .performScrollTo()
            .performClick()

        // Verify we did NOT navigate away
        composeRule.onNodeWithTag("signupButton")
            .assertExists()
    }
}