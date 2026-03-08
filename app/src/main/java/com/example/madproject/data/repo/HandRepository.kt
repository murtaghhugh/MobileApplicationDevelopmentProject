package com.example.madproject.data.repo

import com.example.madproject.data.local.db.BlackjackDatabase
import com.example.madproject.data.local.entities.HandEntity
import com.example.madproject.data.local.entities.ShoeStateEntity
import kotlinx.coroutines.flow.Flow

class HandRepository(
    private val db: BlackjackDatabase
) {
    fun observeLastHands(limit: Int = 20): Flow<List<HandEntity>> {
        return db.handDao().observeLastHands(limit)
    }

    fun observeHands(mode: String?, ascending: Boolean, limit: Int = 20): Flow<List<HandEntity>> {
        return db.handDao().observeHands(mode, ascending, limit)
    }

    suspend fun insertHand(hand: HandEntity): Long {
        return db.handDao().insertHand(hand)
    }

    suspend fun clearAllHands() {
        db.handDao().clearAllHands()
    }

    suspend fun getMostRecentHand(): HandEntity? {
        return db.handDao().getMostRecentHand()
    }

    // --- shoe state ---
    suspend fun getShoeState(mode: String): ShoeStateEntity? {
        return db.shoeStateDao().get(mode)
    }

    suspend fun saveShoeState(state: ShoeStateEntity) {
        db.shoeStateDao().upsert(state)
    }

    suspend fun clearShoeState(mode: String) {
        db.shoeStateDao().clearMode(mode)
    }

    suspend fun getRecentHandsList(limit: Int): List<HandEntity> {
        return db.handDao().getRecentHandsList(limit)
    }

    suspend fun clearAllGameData() {
        db.handDao().clearAllHands()
        db.shoeStateDao().clear()
    }
}