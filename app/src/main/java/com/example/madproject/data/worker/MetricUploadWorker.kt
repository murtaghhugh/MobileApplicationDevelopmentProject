// AI-assisted: WorkManager scaffolding and constraint configuration
package com.example.madproject.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.madproject.MadProjectApp
import com.example.madproject.data.remote.model.MetricEvent
import kotlinx.serialization.json.Json

class MetricUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as MadProjectApp
        val pending = app.pendingUploadDao.getAll()
        if (pending.isEmpty()) return Result.success()

        var allSucceeded = true
        for (entity in pending) {
            val metrics = Json.decodeFromString<List<MetricEvent>>(entity.metricsJson)
            if (app.metricsRepository.attemptUpload(metrics).isSuccess) {
                app.pendingUploadDao.deleteById(entity.id)
            } else {
                allSucceeded = false
            }
        }
        return if (allSucceeded) Result.success() else Result.retry()
    }
}
