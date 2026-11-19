
package com.example.playlist_maker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("SELECT * FROM track_table ORDER BY addedAt DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getFavoriteTrackIds(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId)")
    fun isFavorite(trackId: Long): Flow<Boolean>
}
