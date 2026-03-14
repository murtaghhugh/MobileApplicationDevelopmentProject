// AI-assisted: UI improvements
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
    val error: String? = null,
    val username: String = "",
    val email: String = ""
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
                val raw = result.exceptionOrNull()?.message ?: "Sign up failed"
                val error = if (raw.contains("already registered", ignoreCase = true)) {
                    "An account with this email already exists. Please log in instead."
                } else if (raw.contains("duplicate key", ignoreCase = true) || raw.contains("unique constraint", ignoreCase = true)) {
                    "This username is already taken. Please choose another."
                } else raw
                AuthUiState(error = error)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val result = authRepository.getProfile()
            _uiState.value = if (result.isSuccess) {
                val profile = result.getOrThrow()
                _uiState.value.copy(
                    username = profile.username,
                    email = profile.email
                )
            } else {
                _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }

    fun currentUserId(): String? = authRepository.currentUserId()
}