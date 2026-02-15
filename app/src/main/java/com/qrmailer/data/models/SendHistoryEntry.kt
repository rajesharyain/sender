package com.qrmailer.data.models

/**
 * A recorded send: recipient, file count, optional sender label, and timestamp.
 */
data class SendHistoryEntry(
    val recipientEmail: String,
    val fileCount: Int,
    val senderLabel: String,
    val timestampMillis: Long
) {
    fun dateDisplay(): String {
        val diff = System.currentTimeMillis() - timestampMillis
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} min ago"
            diff < 86400_000 -> "${diff / 3600_000} h ago"
            else -> java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(timestampMillis))
        }
    }
}
