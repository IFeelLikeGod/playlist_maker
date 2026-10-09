package com.example.playlist_maker.domain.usecase

import com.example.playlist_maker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}