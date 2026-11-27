package com.example.playlist_maker.domain.db

import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    fun getPlaylists(): Flow<List<Playlist>>
}
