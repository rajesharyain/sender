package com.qrmailer.ui.qrOwner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeDisplayScreen(
    email: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val qrBitmap = remember(email) { QrCodeGenerator.generateQrBitmap(email) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your QR Code") },
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
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR code for $email",
                    modifier = Modifier.size(280.dp)
                )
            } else {
                Text(
                    "Could not generate QR code",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = email,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "This QR can be scanned multiple times by anyone to send documents to you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { downloadQr(context, qrBitmap, email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = qrBitmap != null
            ) {
                Text("Download QR")
            }
            FilledTonalButton(
                onClick = { shareQr(context, qrBitmap, email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = qrBitmap != null
            ) {
                Text("Share QR")
            }
        }
    }
}

private fun downloadQr(context: Context, bitmap: Bitmap?, email: String) {
    if (bitmap == null) return
    try {
        val filename = "qrmailer_${email.replace("[^a-zA-Z0-9]".toRegex(), "_")}.png"
        val outputStream: OutputStream?
        val uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            }
            val resolver = context.contentResolver
            uri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            outputStream = uri?.let { resolver.openOutputStream(it) }
        } else {
            @Suppress("DEPRECATION")
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val qrDir = File(picturesDir, "QRMailer").apply { mkdirs() }
            outputStream = FileOutputStream(File(qrDir, filename))
            uri = null
        }
        outputStream?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        Toast.makeText(context, "Saved to Pictures/QRMailer", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Could not save: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareQr(context: Context, bitmap: Bitmap?, email: String) {
    if (bitmap == null) return
    try {
        val cacheDir = File(context.cacheDir, "shared")
        cacheDir.mkdirs()
        val file = File(cacheDir, "qrmailer_qr.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share QR code"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not share: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
