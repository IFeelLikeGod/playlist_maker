package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.room.Room

import com.example.playlist_maker.data.db.AppDatabase
import com.example.playlist_maker.data.db.convertors.PlaylistDbConvertor
import com.example.playlist_maker.data.db.convertors.TrackDbConvertor
import com.example.playlist_maker.data.db.repository.FavoritesRepositoryImpl
import com.example.playlist_maker.data.db.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker.data.player.PlayerRepositoryImpl
import com.example.playlist_maker.data.search.network.ITunesApi
import com.example.playlist_maker.data.search.network.NetworkStateProvider
import com.example.playlist_maker.data.search.repository.HistoryRepositoryImpl
import com.example.playlist_maker.data.search.repository.TrackRepositoryImpl
import com.example.playlist_maker.data.settings.repository.SettingsRepositoryImpl
import com.example.playlist_maker.data.sharing.ExternalNavigatorImpl

import com.example.playlist_maker.domain.db.FavoritesInteractor
import com.example.playlist_maker.domain.db.FavoritesInteractorImpl
import com.example.playlist_maker.domain.db.FavoritesRepository
import com.example.playlist_maker.domain.db.PlaylistsInteractor
import com.example.playlist_maker.domain.db.PlaylistsInteractorImpl
import com.example.playlist_maker.domain.db.PlaylistsRepository
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.player.repository.PlayerRepository
import com.example.playlist_maker.domain.player.usecase.PlayerInteractor
import com.example.playlist_maker.domain.player.usecase.PlayerInteractorImpl
import com.example.playlist_maker.domain.search.repository.HistoryRepository
import com.example.playlist_maker.domain.search.repository.TrackRepository
import com.example.playlist_maker.domain.search.usecase.HistoryInteractor
import com.example.playlist_maker.domain.search.usecase.HistoryInteractorImpl
import com.example.playlist_maker.domain.search.usecase.TracksInteractor
import com.example.playlist_maker.domain.search.usecase.TracksInteractorImpl
import com.example.playlist_maker.domain.settings.repository.SettingsRepository
import com.example.playlist_maker.domain.settings.usecase.SettingsInteractor
import com.example.playlist_maker.domain.settings.usecase.SettingsInteractorImpl
import com.example.playlist_maker.domain.sharing.navigator.ExternalNavigator
import com.example.playlist_maker.domain.sharing.usecase.SharingInteractor
import com.example.playlist_maker.domain.sharing.usecase.SharingInteractorImpl

import com.example.playlist_maker.presentation.library.favorites.FavoritesViewModel
import com.example.playlist_maker.presentation.library.playlists.NewPlaylistViewModel
import com.example.playlist_maker.presentation.library.playlists.PlaylistsViewModel
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

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { TrackDbConvertor() }
    factory { PlaylistDbConvertor(get()) }

    // SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )
    }

    // Retrofit
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single {
        NetworkStateProvider(androidContext())
    }

    // Search Repository
    single<TrackRepository> {
        TrackRepositoryImpl(get(), get(), get())
    }

    // History Repository
    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }

    // Favorites Repository
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    // Playlists Repository
    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }

    // Settings Repository
    single<SettingsRepository> {
        val nightMask = androidContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        SettingsRepositoryImpl(
            get(),
            nightMask == Configuration.UI_MODE_NIGHT_YES
        )
    }

    // External Navigator
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    // Player Repository
    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}


val domainModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
}


val presentationModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }

    viewModel {
        SettingsViewModel(
            get(),
            get()
        )
    }

    // PlayerViewModel с поддержкой избранного и плейлистов
    viewModel { (previewUrl: String?, track: Track) ->
        PlayerViewModel(
            previewUrl,
            track,
            get(),
            get(),
            get()
        )
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}
