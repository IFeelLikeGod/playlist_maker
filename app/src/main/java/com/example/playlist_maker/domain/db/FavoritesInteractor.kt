package com.example.playlist_maker.domain.db

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun isFavorite(trackId: Long): Flow<Boolean>
}
