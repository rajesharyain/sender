package com.qrmailer.data.repository

import android.content.Context
import com.qrmailer.data.models.SelectedFile
import com.qrmailer.data.network.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Sends email via the in-app API (multipart to, subject, senderName, files).
 * Resolves content URIs to bytes and uploads them.
 */
class SendEmailRepository(
    private val api = RetrofitProvider.sendEmailApi
) {

    sealed class Result {
        data object Success : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun sendEmail(
        context: Context,
        to: String,
        subject: String,
        senderName: String,
        files: List<SelectedFile>
    ): Result = withContext(Dispatchers.IO) {
        try {
            val toBody = to.toRequestBody("text/plain".toMediaTypeOrNull())
            val subjectBody = subject.toRequestBody("text/plain".toMediaTypeOrNull())
            val senderBody = senderName.toRequestBody("text/plain".toMediaTypeOrNull())
            val parts = files.map { selected ->
                val bytes = context.contentResolver.openInputStream(selected.uri)?.use { it.readBytes() }
                    ?: throw IOException("Cannot read file: ${selected.name}")
                val contentType = context.contentResolver.getType(selected.uri) ?: "application/octet-stream"
                MultipartBody.Part.createFormData(
                    "files",
                    selected.name,
                    bytes.toRequestBody(contentType.toMediaTypeOrNull())
                )
            }
            val response = api.sendEmail(toBody, subjectBody, senderBody, parts)
            if (response.isSuccessful) Result.Success else Result.Error(response.message() ?: "Send failed")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}
