
package com.example.playlist_maker.presentation.library.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.db.FavoritesInteractor
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.launch

sealed interface FavoritesState {

    object Empty : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState
}

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData =
        MutableLiveData<FavoritesState>()

    fun observeState(): LiveData<FavoritesState> = stateLiveData

    init {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks()
                .collect { tracks ->

                    stateLiveData.postValue(
                        if (tracks.isEmpty()) {
                            FavoritesState.Empty
                        } else {
                            FavoritesState.Content(tracks)
                        }
                    )
                }
        }
    }
}
