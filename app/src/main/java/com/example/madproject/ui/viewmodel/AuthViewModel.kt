package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.remote.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            _uiState.value = if (result.isSuccess) {
                AuthUiState(success = true)
            } else {
                AuthUiState(
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val result = authRepository.signUp(username, email, password)
            _uiState.value = if (result.isSuccess) {
                AuthUiState(success = true)
            } else {
                AuthUiState(
                    error = result.exceptionOrNull()?.message ?: "Sign up failed"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun currentUserId(): String? = authRepository.currentUserId()
}