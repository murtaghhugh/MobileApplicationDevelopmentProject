package com.example.madproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.madproject.data.local.entities.ShoeStateEntity

@Dao
interface ShoeStateDao {
    @Query("SELECT * FROM shoe_state WHERE id = 1")
    suspend fun get(): ShoeStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: ShoeStateEntity)

    @Query("DELETE FROM shoe_state WHERE id = 1")
    suspend fun clear()
}