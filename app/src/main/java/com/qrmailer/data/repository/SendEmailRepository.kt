package com.qrmailer.data.repository

import android.content.Context
import com.qrmailer.data.models.SelectedFile
import com.qrmailer.data.network.SendEmailApi
import com.qrmailer.data.network.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sends email via the in-app API (multipart to, subject, senderName, files).
 * Resolves content URIs to bytes and uploads them.
 */
class SendEmailRepository(
    private val api: SendEmailApi = RetrofitProvider.sendEmailApi
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
            if (response.isSuccessful) Result.Success else Result.Error(userFriendlySendError(response.code()))
        } catch (e: Exception) {
            Result.Error(userFriendlyMessage(e))
        }
    }

    private fun userFriendlyMessage(e: Exception): String = when (e) {
        is UnknownHostException, is ConnectException ->
            "No internet connection. Check your network and try again."
        is SocketTimeoutException ->
            "Request timed out. Check your connection and try again."
        is HttpException -> userFriendlySendError(e.code())
        is IOException -> when {
            e.message?.contains("read", ignoreCase = true) == true ->
                "Could not read a file. It may have been moved or deleted."
            else -> "Connection problem. Please try again."
        }
        else -> "Something went wrong. Please try again."
    }

    private fun userFriendlySendError(code: Int): String = when (code) {
        in 400..499 -> "Request was rejected. Please check and try again."
        in 500..599 -> "Server error. Please try again later."
        else -> "Could not send. Please try again."
    }
}
