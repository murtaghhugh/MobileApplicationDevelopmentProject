package com.example.madproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.madproject.data.local.entities.HandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHand(hand: HandEntity): Long

    @Query("DELETE FROM hands")
    suspend fun clearAllHands()

    @Query("""
        SELECT * FROM hands
        ORDER BY playedAtEpochMs DESC
        LIMIT :limit
    """)
    fun observeLastHands(limit: Int = 20): Flow<List<HandEntity>>

    @Query("""
        SELECT * FROM hands
        WHERE (:mode IS NULL OR mode = :mode)
        ORDER BY
            CASE WHEN :ascending = 1 THEN playedAtEpochMs END ASC,
            CASE WHEN :ascending = 0 THEN playedAtEpochMs END DESC
        LIMIT :limit
    """)
    fun observeHands(
        mode: String?,
        ascending: Boolean,
        limit: Int = 20
    ): Flow<List<HandEntity>>

    @Query("""
        SELECT * FROM hands
        ORDER BY playedAtEpochMs DESC
        LIMIT 1
    """)
    suspend fun getMostRecentHand(): HandEntity?

    @Query("SELECT * FROM hands ORDER BY playedAtEpochMs DESC LIMIT :limit")
    suspend fun getRecentHandsList(limit: Int): List<HandEntity>
}