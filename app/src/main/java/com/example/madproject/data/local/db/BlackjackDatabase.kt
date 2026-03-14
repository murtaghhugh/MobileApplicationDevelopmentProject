// AI-assisted development note:
// AI tools were consulted for guidance on structuring the Room database layer,
// including DAO usage and repository patterns. The schema, queries, and data
// flow were implemented and verified by the developer.

package com.example.madproject.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.madproject.data.local.dao.HandDao
import com.example.madproject.data.local.dao.PendingUploadDao
import com.example.madproject.data.local.dao.ShoeStateDao
import com.example.madproject.data.local.entities.HandEntity
import com.example.madproject.data.local.entities.PendingUploadEntity
import com.example.madproject.data.local.entities.ShoeStateEntity

@Database(
    entities = [HandEntity::class, ShoeStateEntity::class, PendingUploadEntity::class],
    version = 7,
    exportSchema = false
)
abstract class BlackjackDatabase : RoomDatabase() {
    abstract fun handDao(): HandDao
    abstract fun shoeStateDao(): ShoeStateDao
    abstract fun pendingUploadDao(): PendingUploadDao
}