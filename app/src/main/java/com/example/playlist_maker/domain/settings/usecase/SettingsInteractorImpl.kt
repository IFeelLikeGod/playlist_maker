package com.example.playlist_maker.domain.settings.usecase

import com.example.playlist_maker.domain.models.ThemeSettings
import com.example.playlist_maker.domain.settings.usecase.SettingsInteractor
import com.example.playlist_maker.domain.settings.repository.SettingsRepository

class SettingsInteractorImpl (private val repository: SettingsRepository) : SettingsInteractor {
    override fun getThemeSettings() = repository.getThemeSettings()
    override fun updateThemeSetting(settings: ThemeSettings) = repository.updateThemeSetting(settings)
}