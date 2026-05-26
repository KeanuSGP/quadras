package com.example.quadras

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    private const val SUPABASE_URL = "https://duoiynnyrgcdxyqdhlds.supabase.co"

    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR1b2l5bm55cmdjZHh5cWRobGRzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ5NzY0MjQsImV4cCI6MjA5MDU1MjQyNH0.izReIXzFHoIyvmJayOC7UNaSP5p0-v8bFyPvYTbLiIE"

    val instance = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)

        install(Auth)
    }
}