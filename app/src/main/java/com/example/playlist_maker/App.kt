package com.example.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.di.dataModule
import com.example.playlist_maker.di.domainModule
import com.example.playlist_maker.di.presentationModule
import com.example.playlist_maker.domain.search.usecase.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

class App : Application(), KoinComponent {

    private val settingsInteractor: SettingsInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }

        val settings = settingsInteractor.getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (settings.darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}