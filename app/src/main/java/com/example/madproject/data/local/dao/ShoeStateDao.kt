package com.example.madproject.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.madproject.data.local.entities.ShoeStateEntity

@Dao
interface ShoeStateDao {

    @Query("SELECT * FROM shoe_state WHERE selectedMode = :mode LIMIT 1")
    suspend fun get(mode: String): ShoeStateEntity?

    @Upsert
    suspend fun upsert(state: ShoeStateEntity)

    @Query("DELETE FROM shoe_state")
    suspend fun clear()

    @Query("DELETE FROM shoe_state WHERE selectedMode = :mode")
    suspend fun clearMode(mode: String)
}