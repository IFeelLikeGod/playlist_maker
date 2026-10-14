package com.example.playlist_maker.data.search.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.search.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(
    private val preferences: SharedPreferences,
    private val gson: Gson
) : HistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history_key"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun getHistory(): List<Track> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun write(tracks: List<Track>) {
        preferences.edit { putString(HISTORY_KEY, gson.toJson(tracks)) }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        write(history.take(MAX_HISTORY_SIZE))
    }

    override fun clearHistory() {
        preferences.edit { remove(HISTORY_KEY) }
    }
}