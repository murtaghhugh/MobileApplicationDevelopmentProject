// AI-assisted: WorkManager scaffolding and constraint configuration
package com.example.madproject

import android.app.Application
import com.example.madproject.data.local.DeviceIdProvider
import com.example.madproject.data.local.dao.PendingUploadDao
import com.example.madproject.data.local.db.BlackjackDatabase
import com.example.madproject.data.local.di.DatabaseModule
import com.example.madproject.data.remote.SupabaseProvider
import com.example.madproject.data.remote.auth.AuthRepository
import com.example.madproject.data.repo.HandRepository
import com.example.madproject.data.repo.MetricsRepository
import com.example.madproject.data.session.SessionManager

class MadProjectApp : Application() {

    val database: BlackjackDatabase by lazy { DatabaseModule.provideDatabase(this) }

    val handRepository: HandRepository by lazy { HandRepository(database) }

    val authRepository: AuthRepository by lazy { AuthRepository() }

    val pendingUploadDao: PendingUploadDao by lazy { database.pendingUploadDao() }

    val metricsRepository: MetricsRepository by lazy {
        MetricsRepository(SupabaseProvider.client, this, pendingUploadDao)
    }

    val deviceIdProvider: DeviceIdProvider by lazy { DeviceIdProvider(this) }

    val sessionManager: SessionManager by lazy { SessionManager() }
}