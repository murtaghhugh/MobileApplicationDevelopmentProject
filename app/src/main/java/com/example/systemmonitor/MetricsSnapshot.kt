package com.example.systemmonitor

data class MetricsSnapshot(
    val batteryPercentage: Int,
    val isCharging: Boolean,
    val networkType: String,
    val ramUsedMb: Long,
    val cpuUsagePercent: Float
)
