package com.example.playlist_maker
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        darkTheme = prefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dark_theme", darkThemeEnabled).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}