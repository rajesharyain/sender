package com.qrmailer.ui.sendOptions

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qrmailer.data.models.MAX_ATTACHMENTS_TOTAL_BYTES
import com.qrmailer.data.models.SelectedFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDocumentsScreen(
    recipientEmail: String,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver

    val files = remember { mutableStateListOf<SelectedFile>() }
    var sizeExceededError by remember { mutableStateOf(false) }

    val totalBytes = files.sumOf { it.sizeBytes }
    val totalDisplay = if (totalBytes < 1024) "$totalBytes B"
        else if (totalBytes < 1024 * 1024) "${totalBytes / 1024} KB"
        else "${totalBytes / (1024 * 1024)} MB"
    val overLimit = totalBytes > MAX_ATTACHMENTS_TOTAL_BYTES
    val canProceed = files.isNotEmpty() && !overLimit

    fun resolveFile(uri: Uri): SelectedFile? {
        val name = run {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else uri.lastPathSegment ?: "file"
                } else uri.lastPathSegment ?: "file"
            } ?: uri.lastPathSegment ?: "file"
        }
        val size = run {
            contentResolver.openFileDescriptor(uri, "r")?.use { pfd -> pfd.statSize } ?: 0L
        }
        return SelectedFile(uri = uri, name = name, sizeBytes = size)
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        sizeExceededError = false
        uris.forEach { uri ->
            val file = resolveFile(uri) ?: return@forEach
            val newTotal = files.sumOf { it.sizeBytes } + file.sizeBytes
            if (newTotal <= MAX_ATTACHMENTS_TOTAL_BYTES) {
                files.add(file)
            } else {
                sizeExceededError = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Documents") },
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
                .fillMaxSize()
        ) {
            Text(
                text = "To: $recipientEmail",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { pickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Choose Files")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total: $totalDisplay / 15 MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (overLimit) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (sizeExceededError || overLimit) {
                Text(
                    text = "Total size must be 15 MB or less.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(files) { index, file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = file.sizeDisplay(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { files.removeAt(index) }) {
                            Text("✕", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    SendSessionState.setSession(recipientEmail, files.toList())
                    onNext()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canProceed
            ) {
                Text("Next")
            }
        }
    }
}
