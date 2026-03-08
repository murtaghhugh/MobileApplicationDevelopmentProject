package com.example.madproject.data.session

import java.util.UUID

class SessionManager {
    private var currentSessionId: String = UUID.randomUUID().toString()

    fun getSessionId(): String = currentSessionId

    fun startNewSession() {
        currentSessionId = UUID.randomUUID().toString()
    }
}