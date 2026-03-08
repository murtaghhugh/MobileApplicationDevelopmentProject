package com.example.madproject.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetricEvent(
    @SerialName("user_id")
    val userId: String,

    @SerialName("device_id")
    val deviceId: String,

    @SerialName("session_id")
    val sessionId: String,

    @SerialName("hand_id")
    val handId: String,

    val source: String,

    @SerialName("metric_name")
    val metricName: String,

    val value: Double,

    val unit: String? = null,

    @SerialName("client_utc_offset_minutes")
    val clientUtcOffsetMinutes: Int? = null
)