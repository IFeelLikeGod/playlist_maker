package com.example.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.creator.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val settings = Creator.provideSettingsInteractor(this).getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (settings.darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}