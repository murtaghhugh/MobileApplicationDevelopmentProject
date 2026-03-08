package com.example.madproject.data.remote.mapper

import com.example.madproject.data.remote.model.MetricEvent
import com.example.madproject.ui.util.getClientUtcOffsetMinutes

object MetricNames {
    const val BALANCE = "balance"
    const val BET_AMOUNT = "bet_amount"
    const val RUNNING_COUNT = "running_count"
    const val TRUE_COUNT = "true_count"
    const val WIN_RATE = "win_rate"
    const val MODE_CODE = "mode_code"
    const val SHOE_NUMBER = "shoe_number"
    const val PLAYER_TOTAL = "player_total"
    const val DEALER_TOTAL = "dealer_total"
    const val OUTCOME_CODE = "outcome_code"
}

object ModeCodes {
    const val BEGINNER = 1.0
    const val INTERMEDIATE = 2.0
    const val ADVANCED = 3.0
}

object OutcomeCodes {
    const val LOSS = -1.0
    const val PUSH = 0.0
    const val WIN = 1.0
    const val BLACKJACK_WIN = 2.0
}

data class HandMetricsSnapshot(
    val userId: String,
    val deviceId: String,
    val sessionId: String,
    val handId: String,
    val balance: Double,
    val betAmount: Double,
    val runningCount: Double,
    val trueCount: Double,
    val winRate: Double,
    val modeCode: Double,
    val shoeNumber: Double,
    val playerTotal: Double,
    val dealerTotal: Double,
    val outcomeCode: Double
)

object MetricsBuilder {

    private const val SOURCE = "android_blackjack"

    fun build(snapshot: HandMetricsSnapshot): List<MetricEvent> {
        val offset = getClientUtcOffsetMinutes()

        return listOf(
            metric(snapshot, MetricNames.BALANCE, snapshot.balance, "credits", offset),
            metric(snapshot, MetricNames.BET_AMOUNT, snapshot.betAmount, "credits", offset),
            metric(snapshot, MetricNames.RUNNING_COUNT, snapshot.runningCount, "count", offset),
            metric(snapshot, MetricNames.TRUE_COUNT, snapshot.trueCount, "count", offset),
            metric(snapshot, MetricNames.WIN_RATE, snapshot.winRate, "percent", offset),
            metric(snapshot, MetricNames.MODE_CODE, snapshot.modeCode, "code", offset),
            metric(snapshot, MetricNames.SHOE_NUMBER, snapshot.shoeNumber, "index", offset),
            metric(snapshot, MetricNames.PLAYER_TOTAL, snapshot.playerTotal, "points", offset),
            metric(snapshot, MetricNames.DEALER_TOTAL, snapshot.dealerTotal, "points", offset),
            metric(snapshot, MetricNames.OUTCOME_CODE, snapshot.outcomeCode, "code", offset)
        )
    }

    private fun metric(
        snapshot: HandMetricsSnapshot,
        name: String,
        value: Double,
        unit: String?,
        offset: Int
    ): MetricEvent {
        return MetricEvent(
            userId = snapshot.userId,
            deviceId = snapshot.deviceId,
            sessionId = snapshot.sessionId,
            handId = snapshot.handId,
            source = SOURCE,
            metricName = name,
            value = value,
            unit = unit,
            clientUtcOffsetMinutes = offset
        )
    }
}