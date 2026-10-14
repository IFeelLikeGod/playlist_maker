package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.playlist_maker.data.player.PlayerRepositoryImpl
import com.example.playlist_maker.data.search.network.ITunesApi
import com.example.playlist_maker.data.search.network.NetworkStateProvider
import com.example.playlist_maker.data.search.repository.HistoryRepositoryImpl
import com.example.playlist_maker.data.search.repository.TrackRepositoryImpl
import com.example.playlist_maker.data.sharing.ExternalNavigatorImpl
import com.example.playlist_maker.domain.player.repository.PlayerRepository
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
import com.example.playlist_maker.presentation.player.PlayerViewModel
import com.example.playlist_maker.presentation.search.SearchViewModel
import com.example.playlist_maker.presentation.settings.SettingsViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single { Gson() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
    single { NetworkStateProvider(androidContext()) }

    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get(), get()) }

    single<SettingsRepository> {
        val nightMask = androidContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        SettingsRepositoryImpl(get(), nightMask == Configuration.UI_MODE_NIGHT_YES)
    }

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    single<PlayerRepository> { PlayerRepositoryImpl() }
}
val domainModule = module{
    factory<TracksInteractor>{ TracksInteractorImpl(get()) }
    factory<HistoryInteractor>{ HistoryInteractorImpl(get()) }
    factory<SettingsInteractor>{ SettingsInteractorImpl(get()) }
    factory<SharingInteractor>{ SharingInteractorImpl(get()) }
    factory<PlayerInteractor>{ PlayerInteractorImpl(get()) }
}
val presentationModule = module{
    viewModel { SearchViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel {(privateUrl: String?) -> PlayerViewModel(get(), get()) }
}









