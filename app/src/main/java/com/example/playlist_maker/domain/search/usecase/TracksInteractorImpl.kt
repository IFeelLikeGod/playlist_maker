package com.example.playlist_maker.domain.search.usecase

import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.search.repository.TrackRepository

class TracksInteractorImpl(
    private val repository: TrackRepository
) : TracksInteractor {

    override fun search(query: String, consumer: TracksInteractor.TracksConsumer) {
        repository.search(query, object : TrackRepository.TracksConsumer {
            override fun consume(foundTracks: List<Track>, errorMessage: String?) {
                consumer.consume(foundTracks, errorMessage)
            }
        })
    }
}