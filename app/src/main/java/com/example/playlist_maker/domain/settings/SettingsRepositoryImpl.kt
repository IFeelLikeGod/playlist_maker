package com.example.playlist_maker.domain.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlist_maker.domain.models.ThemeSettings
import com.example.playlist_maker.domain.search.repository.SettingsRepository


class SettingsRepositoryImpl (private val prefs: SharedPreferences,
                              private val systemDarkTheme: Boolean) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings =
        ThemeSettings(prefs.getBoolean(DARK_THEME_KEY, systemDarkTheme))
    override fun updateThemeSetting(settings: ThemeSettings) {
        prefs.edit { putBoolean(DARK_THEME_KEY, settings.darkTheme) }
    }
    private companion object{
        const val DARK_THEME_KEY = "dark_theme"
    }
}