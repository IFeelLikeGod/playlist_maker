package com.example.playlist_maker.domain.usecase

import com.example.playlist_maker.domain.models.Track

interface TracksInteractor {
    fun search(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, errorMessage: String?)
    }
}