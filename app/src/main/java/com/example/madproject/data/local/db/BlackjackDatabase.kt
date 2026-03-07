package com.example.madproject.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.madproject.data.local.dao.HandDao
import com.example.madproject.data.local.dao.ShoeStateDao
import com.example.madproject.data.local.entities.HandEntity
import com.example.madproject.data.local.entities.ShoeStateEntity

@Database(
    entities = [HandEntity::class, ShoeStateEntity::class],
    version = 5,
    exportSchema = true
)
abstract class BlackjackDatabase : RoomDatabase() {
    abstract fun handDao(): HandDao
    abstract fun shoeStateDao(): ShoeStateDao
}