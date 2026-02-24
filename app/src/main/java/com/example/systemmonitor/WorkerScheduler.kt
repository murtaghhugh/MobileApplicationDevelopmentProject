package com.example.systemmonitor

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkerScheduler {

    private const val WORK_NAME = "MetricsCollectorWorker"

    fun schedule(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val workRequest = PeriodicWorkRequestBuilder<MetricsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME, 
            ExistingPeriodicWorkPolicy.KEEP, 
            workRequest
        )
    }
}
