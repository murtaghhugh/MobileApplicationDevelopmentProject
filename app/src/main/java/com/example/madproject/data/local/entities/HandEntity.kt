package com.example.madproject.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "hands")
data class HandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Timestamp so we can sort newest → oldest
    val playedAtEpochMs: Long,

    // Game mode (Beginner / Intermediate / Advanced) or whatever you call them
    val mode: String,

    // Betting / bankroll tracking (optional now, useful later)
    val bet: Int = 0,

    // Outcome
    val result: String,          // "WIN" | "LOSE" | "PUSH" | "BLACKJACK" | "BUST"
    val playerTotal: Int,
    val dealerTotal: Int,

    // Counting
    val runningCount: Int,
    val trueCount: Double? = null,

    // Shoe info (optional now, useful later)
    val decks: Int? = null,
    val cardsRemaining: Int? = null,

    val balanceAfter: Int
)