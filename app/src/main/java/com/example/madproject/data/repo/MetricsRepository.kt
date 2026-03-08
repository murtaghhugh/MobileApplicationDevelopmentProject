package com.example.madproject.data.repo

import android.util.Log
import com.example.madproject.data.remote.model.MetricEvent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay

class MetricsRepository(
    private val supabase: SupabaseClient
) {
    suspend fun uploadMetrics(metrics: List<MetricEvent>): Result<Unit> {
        return tryUpload(metrics)
    }

    private suspend fun tryUpload(
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
}