package com.example.systemmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.systemmonitor.MetricsWorker.Companion

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WorkerScheduler.schedule(applicationContext)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MetricsScreen()
                }
            }
        }
    }
}

@Composable
fun MetricsScreen() {
    val context = LocalContext.current
    var lastMetrics by remember { mutableStateOf<MetricsSnapshot?>(null) }

    val workManager = WorkManager.getInstance(context)
    val workRequest = remember { OneTimeWorkRequestBuilder<MetricsWorker>().build() }
    val workInfo by workManager.getWorkInfoByIdLiveData(workRequest.id).observeAsState()

    when (workInfo?.state) {
        WorkInfo.State.SUCCEEDED -> {
            val outputData = workInfo?.outputData
            if (outputData != null) {
                lastMetrics = MetricsSnapshot(
                    batteryPercentage = outputData.getInt(Companion.KEY_BATTERY_PERCENT, -1),
                    isCharging = outputData.getBoolean(Companion.KEY_IS_CHARGING, false),
                    networkType = outputData.getString(Companion.KEY_NETWORK_TYPE) ?: "N/A",
                    ramUsedMb = outputData.getLong(Companion.KEY_RAM_USED, -1L),
                    cpuUsagePercent = outputData.getFloat(Companion.KEY_CPU_USAGE, -1f)
                )
            }
        }
        else -> {}
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Worker scheduled")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            workManager.enqueue(workRequest)
        }) {
            Text("Run now")
        }
        Spacer(modifier = Modifier.height(16.dp))
        lastMetrics?.let { 
            Text("Last snapshot:")
            Text("  Battery: ${it.batteryPercentage}% ${if (it.isCharging) "(Charging)" else ""}")
            Text("  Network: ${it.networkType}")
            Text("  RAM Used: ${it.ramUsedMb} MB")
            Text("  CPU Usage: %.2f%%".format(it.cpuUsagePercent))
        }
    }
}
