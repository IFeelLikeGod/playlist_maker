package com.example.playlist_maker.data.db.repository

import com.example.playlist_maker.data.db.AppDatabase
import com.example.playlist_maker.data.db.TrackInPlaylistEntity
import com.example.playlist_maker.data.db.convertors.PlaylistDbConvertor
import com.example.playlist_maker.domain.db.PlaylistsRepository
import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val updatedTrackIds = playlist.trackIds.toMutableList()
        if (!updatedTrackIds.contains(track.trackId)) {
            updatedTrackIds.add(track.trackId)
        }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(updatedPlaylist))

        val trackInPlaylistEntity = TrackInPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        appDatabase.playlistDao().insertTrack(trackInPlaylistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { playlistDbConvertor.map(it) }
        }
    }
}
