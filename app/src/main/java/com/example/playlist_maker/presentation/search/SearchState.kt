package com.example.playlist_maker.presentation.search

import com.example.playlist_maker.domain.models.Track

sealed interface SearchState {
    object Default : SearchState
    object Loading : SearchState
    object Empty : SearchState
    object Error : SearchState
    data class Content(val tracks : List<Track>) : SearchState
    data class History(val tracks : List<Track>) : SearchState
}