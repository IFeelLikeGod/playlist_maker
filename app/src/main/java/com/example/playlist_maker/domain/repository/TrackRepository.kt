package com.example.playlist_maker.domain.repository

import com.example.playlist_maker.domain.models.Track

interface TrackRepository {
    fun search(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, errorMessage: String?)
    }
}