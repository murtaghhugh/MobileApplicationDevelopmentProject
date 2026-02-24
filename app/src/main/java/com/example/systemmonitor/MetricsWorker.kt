package com.example.systemmonitor

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class MetricsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val metricsCollector = MetricsCollector(applicationContext)
        val metrics = metricsCollector.collectMetrics()
        Log.d("MetricsWorker", "Collected metrics: $metrics")

        val outputData = workDataOf(
            KEY_BATTERY_PERCENT to metrics.batteryPercentage,
            KEY_IS_CHARGING to metrics.isCharging,
            KEY_NETWORK_TYPE to metrics.networkType,
            KEY_RAM_USED to metrics.ramUsedMb,
            KEY_CPU_USAGE to metrics.cpuUsagePercent
        )

        return Result.success(outputData)
    }

    companion object {
        const val KEY_BATTERY_PERCENT = "BATTERY_PERCENT"
        const val KEY_IS_CHARGING = "IS_CHARGING"
        const val KEY_NETWORK_TYPE = "NETWORK_TYPE"
        const val KEY_RAM_USED = "RAM_USED"
        const val KEY_CPU_USAGE = "CPU_USAGE"
    }
}
