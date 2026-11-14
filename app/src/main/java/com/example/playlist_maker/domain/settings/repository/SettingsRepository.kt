package com.example.playlist_maker.domain.settings.repository

import com.example.playlist_maker.domain.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings : ThemeSettings)
}