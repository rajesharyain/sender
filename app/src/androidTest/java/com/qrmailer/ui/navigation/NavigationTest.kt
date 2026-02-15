package com.qrmailer.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qrmailer.ui.common.QRMailerNavGraph
import com.qrmailer.ui.common.QRMailerTheme
import com.qrmailer.ui.common.Routes
import com.qrmailer.ui.home.HomeScreenTestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navGraph_startDestination_isHome() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            QRMailerTheme {
                QRMailerNavGraph(navController = navController)
            }
        }
        assertEquals(Routes.HOME, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun navigateFromHome_toGenerateQr_showsQrOwnerScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            QRMailerTheme {
                QRMailerNavGraph(navController = navController)
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_GENERATE_QR).performClick()
        composeTestRule.waitForIdle()
        assertEquals(Routes.QR_OWNER_EMAIL, navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Generate My QR").assertIsDisplayed()
    }

    @Test
    fun navigateFromHome_toScanQr_showsQrScanScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            QRMailerTheme {
                QRMailerNavGraph(navController = navController)
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_SCAN_QR).performClick()
        composeTestRule.waitForIdle()
        assertEquals(Routes.QR_SCAN, navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Scan QR Code").assertIsDisplayed()
    }

    @Test
    fun qrOwnerScreen_back_returnsToHome() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            QRMailerTheme {
                QRMailerNavGraph(navController = navController)
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_GENERATE_QR).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("qr_owner_back").performClick()
        composeTestRule.waitForIdle()
        assertEquals(Routes.HOME, navController.currentBackStackEntry?.destination?.route)
    }
}
