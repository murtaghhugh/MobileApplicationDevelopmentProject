package com.example.madproject.data.remote

import com.example.madproject.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import android.util.Log

object SupabaseProvider {

    init {
        Log.d("SUPABASE_CHECK", "URL = ${BuildConfig.SUPABASE_URL}")
    }
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
