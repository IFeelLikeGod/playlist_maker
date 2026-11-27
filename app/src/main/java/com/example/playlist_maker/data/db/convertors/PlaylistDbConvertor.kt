package com.example.playlist_maker.data.db.convertors

import com.example.playlist_maker.data.db.PlaylistEntity
import com.example.playlist_maker.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            playlistName = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIdsJson = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = gson.fromJson(playlistEntity.tracksIdsJson, type) ?: emptyList()
        return Playlist(
            id = playlistEntity.playlistId,
            name = playlistEntity.playlistName,
            description = playlistEntity.description,
            coverPath = playlistEntity.coverPath,
            trackIds = trackIds,
            tracksCount = playlistEntity.tracksCount
        )
    }
}
