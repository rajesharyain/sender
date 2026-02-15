package com.qrmailer.ui.sendOptions

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSendMethodScreen(
    onSendViaApp: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val email = SendSessionState.recipientEmail
    val subject = SendSessionState.subject
    val files = SendSessionState.selectedFiles

    val hasEmailApp = remember {
        val probe = Intent(Intent.ACTION_SEND).apply { type = "message/rfc822" }
        probe.resolveActivity(context.packageManager) != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to send") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasEmailApp) {
                Text(
                    text = "No email app found. You can send directly from this app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            if (hasEmailApp) {
                Button(
                    onClick = {
                        val intent = buildEmailIntent(context, email, subject, files)
                        try {
                            context.startActivity(Intent.createChooser(intent, "Send with"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open email app", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Use My Email App")
                }
            }
            Button(
                onClick = onSendViaApp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (hasEmailApp) "Send via App instead" else "Send via App")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "To: $email • ${files.size} file(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Builds an intent to send email with To, Subject, and optional attachments.
 * Grants read permission for content URIs so the email app can access files.
 */
private fun buildEmailIntent(
    context: android.content.Context,
    to: String,
    subject: String,
    files: List<com.qrmailer.data.models.SelectedFile>
): Intent {
    val uris = files.map { it.uri }
    return if (uris.size <= 1) {
        Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            if (uris.isNotEmpty()) {
                putExtra(Intent.EXTRA_STREAM, uris.first())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
