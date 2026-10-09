package com.example.playlist_maker.data.repository

import android.content.SharedPreferences
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.playlist_maker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val preferences: SharedPreferences
) : HistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history_key"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun getHistory(): List<Track> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun write(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        preferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        val updatedHistory = history.take(MAX_HISTORY_SIZE)
        write(updatedHistory)
    }

    override fun clearHistory() {
        preferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}