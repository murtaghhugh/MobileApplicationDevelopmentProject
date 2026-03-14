// AI-assisted: WorkManager scaffolding and constraint configuration
package com.example.madproject.data.repo

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.madproject.data.local.dao.PendingUploadDao
import com.example.madproject.data.local.entities.PendingUploadEntity
import com.example.madproject.data.remote.model.MetricEvent
import com.example.madproject.data.worker.MetricUploadWorker
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

class MetricsRepository(
    private val supabase: SupabaseClient,
    private val context: Context,
    private val pendingUploadDao: PendingUploadDao
) {
    suspend fun uploadMetrics(metrics: List<MetricEvent>): Result<Unit> {
        val result = attemptUpload(metrics)
        if (result.isFailure) {
            saveToQueue(metrics)
            enqueueRetryWorker()
        }
        return result
    }

    suspend fun attemptUpload(
        metrics: List<MetricEvent>,
        maxAttempts: Int = 2
    ): Result<Unit> {
        var lastError: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                supabase.postgrest["metrics"].insert(metrics)
                Log.d(
                    "METRICS_UPLOAD",
                    "Uploaded ${metrics.size} metric rows on attempt ${attempt + 1}"
                )
                return Result.success(Unit)
            } catch (e: Exception) {
                lastError = e
                Log.e(
                    "METRICS_UPLOAD",
                    "Upload failed on attempt ${attempt + 1}",
                    e
                )

                if (attempt < maxAttempts - 1) {
                    delay(1000)
                }
            }
        }

        return Result.failure(lastError ?: Exception("Unknown metrics upload failure"))
    }

    private suspend fun saveToQueue(metrics: List<MetricEvent>) {
        val json = Json.encodeToString(ListSerializer(MetricEvent.serializer()), metrics)
        pendingUploadDao.insert(
            PendingUploadEntity(
                metricsJson = json,
                enqueuedAtEpochMs = System.currentTimeMillis()
            )
        )
        Log.d("METRICS_UPLOAD", "Saved ${metrics.size} metric rows to offline queue")
    }

    private fun enqueueRetryWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<MetricUploadWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "metric_upload_retry",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d("METRICS_UPLOAD", "Enqueued MetricUploadWorker for retry on next network connection")
    }
}
