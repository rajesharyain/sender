package com.qrmailer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

object HomeScreenTestTags {
    const val BUTTON_GENERATE_QR = "home_button_generate_qr"
    const val BUTTON_SCAN_QR = "home_button_scan_qr"
}

@Composable
fun HomeScreen(
    onGenerateQr: () -> Unit,
    onScanQr: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QR Mailer",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Share your email via QR or send documents by scanning",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onGenerateQr,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag(HomeScreenTestTags.BUTTON_GENERATE_QR)
        ) {
            Text("Generate My QR")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onScanQr,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag(HomeScreenTestTags.BUTTON_SCAN_QR)
        ) {
            Text("Scan QR & Send Documents")
        }
    }
}
