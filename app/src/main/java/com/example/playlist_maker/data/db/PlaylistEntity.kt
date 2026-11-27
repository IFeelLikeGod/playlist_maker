package com.example.playlist_maker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val tracksCount: Int,
    val coverPath: String?,
    val description: String?,
    val tracksIdsJson: String,
)