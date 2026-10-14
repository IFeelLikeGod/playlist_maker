package com.example.playlist_maker.domain.search.usecase

import com.example.playlist_maker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}