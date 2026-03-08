package com.example.madproject.data.local

import android.content.Context
import java.util.UUID

class DeviceIdProvider(context: Context) {

    private val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) return existing

        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
    }
}