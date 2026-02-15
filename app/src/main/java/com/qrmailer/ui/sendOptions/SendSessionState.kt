package com.qrmailer.ui.sendOptions

import com.qrmailer.data.models.SelectedFile

/** Default subject for documents-shared email. */
const val DEFAULT_EMAIL_SUBJECT = "Documents shared via QR"

/**
 * In-memory state for the current send flow (Phase 4 → 5 → 6).
 * Set by SelectDocumentsScreen when user taps Next; subject set in Review screen.
 */
object SendSessionState {
    var recipientEmail: String = ""
        private set
    var selectedFiles: List<SelectedFile> = emptyList()
        private set
    var subject: String = DEFAULT_EMAIL_SUBJECT
    var senderName: String = ""

    fun setSession(email: String, files: List<SelectedFile>) {
        recipientEmail = email
        selectedFiles = files
        subject = DEFAULT_EMAIL_SUBJECT
        senderName = ""
    }

    fun setSenderName(name: String) {
        senderName = name
    }

    fun clear() {
        recipientEmail = ""
        selectedFiles = emptyList()
        subject = DEFAULT_EMAIL_SUBJECT
        senderName = ""
    }
}
