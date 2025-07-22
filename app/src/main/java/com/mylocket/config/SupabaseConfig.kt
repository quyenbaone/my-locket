package com.mylocket.config

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.AuthConfig
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseConfig {
    private const val SUPABASE_URL = "https://ftqzohcisqzckecvhvez.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ0cXpvaGNpc3F6Y2tlY3ZodmV6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI5OTI5MjYsImV4cCI6MjA2ODU2ODkyNn0.LVJ2TKpFFjpSI-I7MDPunE6ul9TAodhveKToulo64nk"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            // Enable automatic session persistence
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
