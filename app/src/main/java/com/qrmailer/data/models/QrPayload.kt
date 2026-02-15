package com.qrmailer.data.models

import java.net.URLDecoder

/**
 * Format for QR code payload when sharing email.
 * Scanners will open this URI; app can parse email from query param.
 */
object QrPayload {
    const val SCHEME = "qrmailer"
    const val HOST_SEND = "send"
    const val PARAM_EMAIL = "email"

    /**
     * Builds the string to encode in the QR code.
     * Example: qrmailer://send?email=user@example.com
     */
    fun forEmail(email: String): String =
        "$SCHEME://$HOST_SEND?$PARAM_EMAIL=${java.net.URLEncoder.encode(email, Charsets.UTF_8.name())}"

    /**
     * Parses email from a scanned QR payload.
     * Returns the email if the content is a valid qrmailer://send?email=... URI, null otherwise.
     */
    fun parseEmailFromPayload(content: String?): String? {
        if (content.isNullOrBlank()) return null
        val trimmed = content.trim()
        if (!trimmed.startsWith("$SCHEME://")) return null
        return try {
            val queryStart = trimmed.indexOf('?')
            if (queryStart < 0) return null
            val query = trimmed.substring(queryStart + 1)
            val params = query.split('&').mapNotNull { param ->
                val eq = param.indexOf('=')
                if (eq < 0) null else param.substring(0, eq) to param.substring(eq + 1)
            }.toMap()
            val email = params[PARAM_EMAIL] ?: return null
            URLDecoder.decode(email, Charsets.UTF_8.name()).takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
