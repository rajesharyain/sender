package com.qrmailer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qrmailer.ui.history.SendHistoryScreen
import com.qrmailer.ui.home.HomeScreen
import com.qrmailer.ui.qrOwner.EnterEmailScreen
import com.qrmailer.ui.qrOwner.QrCodeDisplayScreen
import com.qrmailer.ui.qrScanner.ConfirmEmailScreen
import com.qrmailer.ui.qrScanner.QrScanScreen
import com.qrmailer.ui.sendOptions.ChooseSendMethodScreen
import com.qrmailer.ui.sendOptions.ReviewScreen
import com.qrmailer.ui.sendOptions.SelectDocumentsScreen
import com.qrmailer.ui.sendOptions.SendSuccessScreen
import com.qrmailer.ui.sendOptions.SenderInfoScreen
import com.qrmailer.ui.sendOptions.SendingScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val HOME = "home"
    const val QR_OWNER_EMAIL = "qr_owner_email"
    const val QR_OWNER_DISPLAY = "qr_owner_display/{email}"
    const val QR_SCAN = "qr_scan"
    const val CONFIRM_EMAIL = "confirm_email/{email}"
    const val SELECT_DOCUMENTS = "select_documents/{email}"
    const val REVIEW = "review"
    const val CHOOSE_SEND_METHOD = "choose_send_method"
    const val SENDER_INFO = "sender_info"
    const val SENDING = "sending"
    const val SEND_SUCCESS = "send_success"
    const val HISTORY = "history"

    fun qrOwnerDisplay(email: String): String =
        "qr_owner_display/${URLEncoder.encode(email, StandardCharsets.UTF_8.toString())}"

    fun confirmEmail(email: String): String =
        "confirm_email/${URLEncoder.encode(email, StandardCharsets.UTF_8.toString())}"

    fun selectDocuments(email: String): String =
        "select_documents/${URLEncoder.encode(email, StandardCharsets.UTF_8.toString())}"
}

@Composable
fun QRMailerNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onGenerateQr = { navController.navigate(Routes.QR_OWNER_EMAIL) },
                onScanQr = { navController.navigate(Routes.QR_SCAN) },
                onHistory = { navController.navigate(Routes.HISTORY) }
            )
        }
        composable(Routes.HISTORY) {
            SendHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.QR_OWNER_EMAIL) {
            EnterEmailScreen(
                onGenerateQr = { email -> navController.navigate(Routes.qrOwnerDisplay(email)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.QR_OWNER_DISPLAY,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: return@composable
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
            QrCodeDisplayScreen(
                email = email,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.QR_SCAN) {
            QrScanScreen(
                onEmailScanned = { email -> navController.navigate(Routes.confirmEmail(email)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.CONFIRM_EMAIL,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: return@composable
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
            ConfirmEmailScreen(
                email = email,
                onContinue = { navController.navigate(Routes.selectDocuments(email)) },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.SELECT_DOCUMENTS,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: return@composable
            val email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())
            SelectDocumentsScreen(
                recipientEmail = email,
                onNext = { navController.navigate(Routes.REVIEW) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.REVIEW) {
            ReviewScreen(
                onNext = { navController.navigate(Routes.CHOOSE_SEND_METHOD) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHOOSE_SEND_METHOD) {
            ChooseSendMethodScreen(
                onSendViaApp = { navController.navigate(Routes.SENDER_INFO) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SENDER_INFO) {
            SenderInfoScreen(
                onSend = { navController.navigate(Routes.SENDING) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SENDING) {
            SendingScreen(
                onSuccess = { navController.navigate(Routes.SEND_SUCCESS) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SEND_SUCCESS) {
            SendSuccessScreen(
                onDone = {
                    com.qrmailer.ui.sendOptions.SendSessionState.clear()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
