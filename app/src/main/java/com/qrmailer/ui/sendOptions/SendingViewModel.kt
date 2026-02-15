package com.qrmailer.ui.sendOptions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmailer.data.repository.SendEmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SendingUiState {
    data object Idle : SendingUiState()
    data object Loading : SendingUiState()
    data object Success : SendingUiState()
    data class Error(val message: String) : SendingUiState()
}

class SendingViewModel(
    private val repository: SendEmailRepository = SendEmailRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<SendingUiState>(SendingUiState.Idle)
    val state: StateFlow<SendingUiState> = _state.asStateFlow()

    fun send(context: Context) {
        if (_state.value == SendingUiState.Loading) return
        val to = SendSessionState.recipientEmail
        val subject = SendSessionState.subject
        val senderName = SendSessionState.senderName.ifEmpty { "Someone" }
        val files = SendSessionState.selectedFiles
        if (to.isBlank() || files.isEmpty()) {
            _state.value = SendingUiState.Error("Recipient and at least one file are required.")
            return
        }
        _state.value = SendingUiState.Loading
        viewModelScope.launch {
            val result = repository.sendEmail(context, to, subject, senderName, files)
            _state.value = when (result) {
                is SendEmailRepository.Result.Success -> SendingUiState.Success
                is SendEmailRepository.Result.Error -> SendingUiState.Error(result.message)
            }
        }
    }

    fun reset() {
        _state.value = SendingUiState.Idle
    }
}
