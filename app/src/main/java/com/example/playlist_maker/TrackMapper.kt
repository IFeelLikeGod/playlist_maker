package com.example.playlist_maker

import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toTrack(): Track {
    return Track(
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.trackTimeMillis),
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country
    )
}

fun Track.getCoverArtwork(): String {
    return this.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}