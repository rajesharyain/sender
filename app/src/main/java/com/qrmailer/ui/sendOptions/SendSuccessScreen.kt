package com.qrmailer.ui.sendOptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.qrmailer.data.models.SendHistoryEntry
import com.qrmailer.data.repository.SendHistoryRepository

@Composable
fun SendSuccessScreen(
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val email = SendSessionState.recipientEmail
    val fileCount = SendSessionState.selectedFiles.size
    val senderLabel = SendSessionState.senderName.ifEmpty { "Someone" }
    var recorded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!recorded) {
            SendHistoryRepository(context).add(
                SendHistoryEntry(
                    recipientEmail = email,
                    fileCount = fileCount,
                    senderLabel = senderLabel,
                    timestampMillis = System.currentTimeMillis()
                )
            )
            recorded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sent!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "To: $email",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$fileCount file(s) delivered",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDone) {
            Text("Done")
        }
    }
}
