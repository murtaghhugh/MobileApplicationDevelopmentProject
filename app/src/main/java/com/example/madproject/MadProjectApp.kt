package com.example.madproject

import android.app.Application
import com.example.madproject.data.local.db.BlackjackDatabase
import com.example.madproject.data.local.di.DatabaseModule
import com.example.madproject.data.repo.HandRepository

class MadProjectApp : Application() {
    // Simple service locator (no Hilt required)
    val database: BlackjackDatabase by lazy { DatabaseModule.provideDatabase(this) }
    val handRepository: HandRepository by lazy { HandRepository(database) }
}