package com.qrmailer.ui.qrOwner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.regex.Pattern

private val EMAIL_PATTERN = Pattern.compile(
    "[a-zA-Z0-9+._%\\-]+@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]*)*"
)

fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return false
    return EMAIL_PATTERN.matcher(email.trim()).matches()
}

object EnterEmailTestTags {
    const val EMAIL_FIELD = "enter_email_field"
    const val GENERATE_BUTTON = "enter_email_generate_button"
    const val ERROR_TEXT = "enter_email_error"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterEmailScreen(
    onGenerateQr: (String) -> Unit,
    onBack: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate My QR") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("qr_owner_back")
                    ) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = null
                },
                label = { Text("Your email") },
                placeholder = { Text("you@example.com") },
                singleLine = true,
                isError = error != null,
                supportingText = error?.let { { Text(it, modifier = Modifier.testTag(EnterEmailTestTags.ERROR_TEXT)) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(EnterEmailTestTags.EMAIL_FIELD)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val trimmed = email.trim()
                    when {
                        trimmed.isBlank() -> error = "Please enter your email"
                        !isValidEmail(trimmed) -> error = "Please enter a valid email address"
                        else -> onGenerateQr(trimmed)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag(EnterEmailTestTags.GENERATE_BUTTON)
            ) {
                Text("Generate QR Code")
            }
        }
    }
}
