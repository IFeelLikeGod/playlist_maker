package com.example.playlist_maker.domain.repository


import com.example.playlist_maker.domain.models.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}