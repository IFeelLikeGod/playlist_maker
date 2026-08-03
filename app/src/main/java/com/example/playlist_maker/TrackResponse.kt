package com.example.playlist_maker

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)