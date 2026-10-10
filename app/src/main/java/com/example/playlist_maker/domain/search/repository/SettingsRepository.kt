package com.example.playlist_maker.domain.search.repository
import com.example.playlist_maker.domain.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings : ThemeSettings)
}