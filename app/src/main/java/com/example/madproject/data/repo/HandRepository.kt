package com.example.madproject.data.repo

import com.example.madproject.data.local.db.BlackjackDatabase
import com.example.madproject.data.local.entities.HandEntity
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
}
