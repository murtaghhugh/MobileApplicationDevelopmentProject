package com.example.madproject.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoe_state")
data class ShoeStateEntity(
    @PrimaryKey val id: Int = 1,          // single-row table
    val decks: Int,
    val cutIndex: Int,
    val dealtCount: Int,
    val runningCount: Int,
    val cardsCsv: String,                 // remaining cards (in order)
    val discardsCsv: String               // discard pile
)