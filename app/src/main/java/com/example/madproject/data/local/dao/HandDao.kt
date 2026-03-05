package com.example.madproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.madproject.data.local.entities.HandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HandDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHand(hand: HandEntity): Long

    @Query("SELECT * FROM hands ORDER BY playedAtEpochMs DESC LIMIT :limit")
    fun observeLastHands(limit: Int = 20): Flow<List<HandEntity>>

    @Query("SELECT * FROM hands ORDER BY playedAtEpochMs DESC LIMIT 1")
    suspend fun getMostRecentHand(): HandEntity?

    @Query("DELETE FROM hands")
    suspend fun clearAllHands()

    @Query("""
    SELECT * FROM hands
    WHERE (:mode IS NULL OR mode = :mode)
    ORDER BY playedAtEpochMs DESC
    LIMIT :limit
""")
    fun observeHandsDesc(mode: String?, limit: Int = 20): Flow<List<HandEntity>>

    @Query("""
    SELECT * FROM hands
    WHERE (:mode IS NULL OR mode = :mode)
    ORDER BY playedAtEpochMs ASC
    LIMIT :limit
""")
    fun observeHandsAsc(mode: String?, limit: Int = 20): Flow<List<HandEntity>>

    fun observeHands(mode: String?, ascending: Boolean, limit: Int = 20): Flow<List<HandEntity>> {
        return if (ascending) observeHandsAsc(mode, limit)
        else observeHandsDesc(mode, limit)
    }
}
