package com.example.playlist_maker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "track_table")
data class TrackInPlayEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)