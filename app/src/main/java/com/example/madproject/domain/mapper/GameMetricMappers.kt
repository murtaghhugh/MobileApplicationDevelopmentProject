package com.example.madproject.domain.mapper

import com.example.madproject.data.remote.mapper.ModeCodes
import com.example.madproject.data.remote.mapper.OutcomeCodes

fun String.toModeCode(): Double = when (this.uppercase()) {
    "BEGINNER" -> ModeCodes.BEGINNER
    "INTERMEDIATE" -> ModeCodes.INTERMEDIATE
    "ADVANCED" -> ModeCodes.ADVANCED
    else -> ModeCodes.BEGINNER
}

fun String.toOutcomeCode(): Double = when (this.uppercase()) {
    "LOSE" -> OutcomeCodes.LOSS
    "PUSH" -> OutcomeCodes.PUSH
    "WIN" -> OutcomeCodes.WIN
    "BLACKJACK" -> OutcomeCodes.BLACKJACK_WIN
    else -> OutcomeCodes.PUSH
}