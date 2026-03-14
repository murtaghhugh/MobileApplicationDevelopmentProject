// AI-assisted: WorkManager scaffolding and constraint configuration
package com.example.madproject.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_uploads")
data class PendingUploadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val metricsJson: String,
    val enqueuedAtEpochMs: Long
)
