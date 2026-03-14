// AI-assisted: WorkManager scaffolding and constraint configuration
package com.example.madproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.madproject.data.local.entities.PendingUploadEntity

@Dao
interface PendingUploadDao {
    @Insert
    suspend fun insert(entity: PendingUploadEntity)

    @Query("SELECT * FROM pending_uploads")
    suspend fun getAll(): List<PendingUploadEntity>

    @Query("DELETE FROM pending_uploads WHERE id = :id")
    suspend fun deleteById(id: Long)
}
