package com.example.playlist_maker.presentation.library.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.db.PlaylistsInteractor
import com.example.playlist_maker.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun createPlaylist(name: String, description: String?, coverPath: String?) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                coverPath = coverPath,
                trackIds = emptyList(),
                tracksCount = 0
            )
            playlistsInteractor.createPlaylist(playlist)
        }
    }
}
