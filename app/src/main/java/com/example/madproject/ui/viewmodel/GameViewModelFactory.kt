package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.madproject.data.local.DeviceIdProvider
import com.example.madproject.data.remote.auth.AuthRepository
import com.example.madproject.data.repo.HandRepository
import com.example.madproject.data.repo.MetricsRepository
import com.example.madproject.data.session.SessionManager

class GameViewModelFactory(
    private val handRepository: HandRepository,
    private val authRepository: AuthRepository,
    private val metricsRepository: MetricsRepository,
    private val deviceIdProvider: DeviceIdProvider,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(
                handRepository = handRepository,
                authRepository = authRepository,
                metricsRepository = metricsRepository,
                deviceIdProvider = deviceIdProvider,
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}