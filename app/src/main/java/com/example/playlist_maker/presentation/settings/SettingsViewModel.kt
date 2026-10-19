package com.example.playlist_maker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.domain.models.ThemeSettings
import com.example.playlist_maker.domain.search.usecase.SettingsInteractor
import com.example.playlist_maker.domain.sharing.usecase.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val themeLiveData = MutableLiveData(settingsInteractor.getThemeSettings())
    fun observeTheme(): LiveData<ThemeSettings> = themeLiveData

    fun onThemeChanged(darkTheme: Boolean) {
        val newSettings = ThemeSettings(darkTheme)
        if (themeLiveData.value == newSettings) return
        settingsInteractor.updateThemeSetting(newSettings)
        themeLiveData.value = newSettings
    }
    fun onShareClicked() = sharingInteractor.shareApp()
    fun onSupportClicked() = sharingInteractor.openSupport()
    fun onTermsClicked() = sharingInteractor.openTerms()
}