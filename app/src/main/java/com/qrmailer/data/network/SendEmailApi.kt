package com.qrmailer.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * API for in-app email sending (Phase 7).
 * POST multipart: to, subject, senderName, and file attachments.
 */
interface SendEmailApi {

    @Multipart
    @POST("send-email")
    suspend fun sendEmail(
        @Part("to") to: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("senderName") senderName: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<Unit>
}
