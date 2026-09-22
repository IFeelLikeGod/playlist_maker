package com.example.playlist_maker.domain.usecase

import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.HistoryRepository

class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}