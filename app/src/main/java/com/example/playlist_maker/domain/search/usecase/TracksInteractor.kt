package com.example.playlist_maker.domain.search.usecase

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(query: String) : Flow<Pair<List<Track>?, String?>>

}