package com.example.playlist_maker.creator

import android.content.Context
import android.content.res.Configuration
import com.example.playlist_maker.data.player.PlayerRepositoryImpl
import com.example.playlist_maker.data.search.network.NetworkStateProvider
import com.example.playlist_maker.data.search.repository.HistoryRepositoryImpl
import com.example.playlist_maker.data.search.repository.TrackRepositoryImpl
import com.example.playlist_maker.data.sharing.ExternalNavigatorImpl
import com.example.playlist_maker.domain.player.usecase.PlayerInteractor
import com.example.playlist_maker.domain.player.usecase.PlayerInteractorImpl
import com.example.playlist_maker.domain.search.repository.HistoryRepository
import com.example.playlist_maker.domain.search.repository.SettingsRepository
import com.example.playlist_maker.domain.search.repository.TrackRepository
import com.example.playlist_maker.domain.search.usecase.HistoryInteractor
import com.example.playlist_maker.domain.search.usecase.HistoryInteractorImpl
import com.example.playlist_maker.domain.search.usecase.SettingsInteractor
import com.example.playlist_maker.domain.search.usecase.SettingsInteractorImpl
import com.example.playlist_maker.domain.search.usecase.TracksInteractor
import com.example.playlist_maker.domain.search.usecase.TracksInteractorImpl
import com.example.playlist_maker.domain.settings.SettingsRepositoryImpl
import com.example.playlist_maker.domain.sharing.navigator.ExternalNavigator
import com.example.playlist_maker.domain.sharing.usecase.SharingInteractor
import com.example.playlist_maker.domain.sharing.usecase.SharingInteractorImpl

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        val networkStateProvider = NetworkStateProvider(context)
        return TrackRepositoryImpl(networkStateProvider)
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return HistoryRepositoryImpl(prefs)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository(context))
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }
    private fun getSettingsRepository(context: Context): SettingsRepository {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val nightMask = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return SettingsRepositoryImpl(prefs, nightMask == Configuration.UI_MODE_NIGHT_YES)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository(context))
    private fun getExternalNavigator(context: Context): ExternalNavigator =
        ExternalNavigatorImpl(context)

    fun provideSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(getExternalNavigator(context))
    fun providePlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(PlayerRepositoryImpl())
}