package com.example.madproject.ui.viewmodel

data class DashboardItem(
    val id: Long,
    val timeLabel: String,
    val mode: String,
    val result: String,
    val runningCount: Int,
    val bet: Int,
    val balanceAfter: Int
)