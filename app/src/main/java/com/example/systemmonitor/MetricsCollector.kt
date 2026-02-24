package com.example.systemmonitor

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import java.io.RandomAccessFile

class MetricsCollector(private val context: Context) {

    fun collectMetrics(): MetricsSnapshot {
        return MetricsSnapshot(
            batteryPercentage = getBatteryPercentage(),
            isCharging = isDeviceCharging(),
            networkType = getNetworkType(),
            ramUsedMb = getRamUsedMb(),
            cpuUsagePercent = getCpuUsage()
        )
    }

    private fun getBatteryPercentage(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun isDeviceCharging(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(null, it)
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun getNetworkType(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return "None"
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "None"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            else -> "None"
        }
    }

    private fun getRamUsedMb(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024)
    }

    private fun getCpuUsage(): Float {
        try {
            val reader1 = RandomAccessFile("/proc/stat", "r")
            val load1 = reader1.readLine().split(" ")
            val idle1 = load1[4].toLong()
            val total1 = load1[1].toLong() + load1[2].toLong() + load1[3].toLong() + load1[4].toLong() + load1[5].toLong() + load1[6].toLong() + load1[7].toLong()
            reader1.close()

            Thread.sleep(1000)

            val reader2 = RandomAccessFile("/proc/stat", "r")
            val load2 = reader2.readLine().split(" ")
            val idle2 = load2[4].toLong()
            val total2 = load2[1].toLong() + load2[2].toLong() + load2[3].toLong() + load2[4].toLong() + load2[5].toLong() + load2[6].toLong() + load2[7].toLong()
            reader2.close()

            val idle = idle2 - idle1
            val total = total2 - total1

            return (total - idle).toFloat() / total * 100
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0f
    }
}
