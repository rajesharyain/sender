package com.qrmailer.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qrmailer.ui.common.QRMailerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_showsTitle() {
        composeTestRule.setContent {
            QRMailerTheme {
                HomeScreen(onGenerateQr = {}, onScanQr = {})
            }
        }
        composeTestRule.onNodeWithText("QR Mailer").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsGenerateQrButton() {
        composeTestRule.setContent {
            QRMailerTheme {
                HomeScreen(onGenerateQr = {}, onScanQr = {})
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_GENERATE_QR).assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate My QR").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsScanQrButton() {
        composeTestRule.setContent {
            QRMailerTheme {
                HomeScreen(onGenerateQr = {}, onScanQr = {})
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_SCAN_QR).assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan QR & Send Documents").assertIsDisplayed()
    }

    @Test
    fun homeScreen_generateQrButton_invokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            QRMailerTheme {
                HomeScreen(
                    onGenerateQr = { clicked = true },
                    onScanQr = {}
                )
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_GENERATE_QR).performClick()
        composeTestRule.waitForIdle()
        assert(clicked)
    }

    @Test
    fun homeScreen_scanQrButton_invokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            QRMailerTheme {
                HomeScreen(
                    onGenerateQr = {},
                    onScanQr = { clicked = true }
                )
            }
        }
        composeTestRule.onNodeWithTag(HomeScreenTestTags.BUTTON_SCAN_QR).performClick()
        composeTestRule.waitForIdle()
        assert(clicked)
    }
}
