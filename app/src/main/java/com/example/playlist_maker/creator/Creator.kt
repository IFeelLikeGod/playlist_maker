package com.example.playlist_maker.creator

import android.content.Context
import com.example.playlist_maker.data.repository.HistoryRepositoryImpl
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.domain.repository.HistoryRepository
import com.example.playlist_maker.domain.repository.TrackRepository
import com.example.playlist_maker.domain.usecase.HistoryInteractor
import com.example.playlist_maker.domain.usecase.HistoryInteractorImpl
import com.example.playlist_maker.domain.usecase.TracksInteractor
import com.example.playlist_maker.domain.usecase.TracksInteractorImpl

object Creator {

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl()
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return HistoryRepositoryImpl(prefs)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }
}