package com.qrmailer.data.models

import android.net.Uri

/**
 * Represents a file selected by the user for sending.
 */
data class SelectedFile(
    val uri: Uri,
    val name: String,
    val sizeBytes: Long
) {
    fun sizeDisplay(): String {
        if (sizeBytes < 1024) return "$sizeBytes B"
        if (sizeBytes < 1024 * 1024) return "${sizeBytes / 1024} KB"
        return "${sizeBytes / (1024 * 1024)} MB"
    }
}

/** Max total size for selected documents: 15 MB */
const val MAX_ATTACHMENTS_TOTAL_BYTES = 15L * 1024 * 1024
