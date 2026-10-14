package com.example.playlist_maker.presentation.player

sealed interface PlayerState {
    object Default : PlayerState
    data class Prepared(val progress : String) : PlayerState
    data class Playing(val progress: String) : PlayerState
    data class Paused(val progress: String) : PlayerState
}