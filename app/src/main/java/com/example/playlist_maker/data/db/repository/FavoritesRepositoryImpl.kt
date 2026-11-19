
package com.example.playlist_maker.data.db.repository

import com.example.playlist_maker.data.db.AppDatabase
import com.example.playlist_maker.data.db.convertors.TrackDbConvertor
import com.example.playlist_maker.domain.db.FavoritesRepository
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao()
            .getFavoriteTracks()
            .map { tracks ->
                tracks.map { track ->
                    trackDbConvertor.map(track)
                }
            }
    }

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(
            trackDbConvertor.map(track)
        )
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(track.trackId)
    }

    override fun isFavorite(trackId: Long): Flow<Boolean> {
        return appDatabase.trackDao().isFavorite(trackId)
    }
}
