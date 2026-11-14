package com.example.playlist_maker.domain.settings.usecase

import com.example.playlist_maker.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings : ThemeSettings)
}