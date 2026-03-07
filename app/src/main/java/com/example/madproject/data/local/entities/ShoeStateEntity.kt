package com.example.madproject.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoe_state")
data class ShoeStateEntity(
    @PrimaryKey val selectedMode: String,

    val decks: Int,
    val cutMin: Double,
    val cutMax: Double,

    val balance: Int,
    val bet: Int,
    val runningCount: Int,

    val cutIndex: Int,
    val dealtCount: Int,

    val cardsCsv: String,
    val discardsCsv: String,

    val playerCardsCsv: String,
    val dealerCardsCsv: String,

    val phase: String,
    val message: String
)