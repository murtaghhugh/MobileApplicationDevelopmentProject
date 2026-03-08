package com.example.madproject.ui.util

import java.time.OffsetDateTime

fun getClientUtcOffsetMinutes(): Int {
    return OffsetDateTime.now().offset.totalSeconds / 60
}