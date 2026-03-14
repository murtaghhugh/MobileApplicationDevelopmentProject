package com.example.madproject.data.remote
// AI-assisted development note:
// AI tools were used to assist with configuring the Supabase client and
// debugging authentication setup. The final integration with the application
// architecture and secure configuration of environment variables were completed by the developer.

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
