package com.example.madproject.data.local.di

import android.content.Context
import androidx.room.Room
import com.example.madproject.data.local.db.BlackjackDatabase

object DatabaseModule {
    @Volatile private var INSTANCE: BlackjackDatabase? = null

    fun provideDatabase(appContext: Context): BlackjackDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                appContext,
                BlackjackDatabase::class.java,
                "blackjack.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}