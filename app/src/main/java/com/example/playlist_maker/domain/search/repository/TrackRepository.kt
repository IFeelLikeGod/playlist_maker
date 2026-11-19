package com.example.playlist_maker.domain.search.repository

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface TrackRepository {
    fun search(query: String): Flow<Pair<List<Track>?, String?>>
}