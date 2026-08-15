package com.example.playlist_maker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val preferences: SharedPreferences) {

    companion object {
        private const val HISTORY_KEY = "search_history_key"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun read(): List<Track> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun write(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        preferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
    fun add(track: Track){
        val history = read().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        val updatedHistory = history.take(MAX_HISTORY_SIZE)
        write(updatedHistory)
    }

    fun clear() {
        preferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

}