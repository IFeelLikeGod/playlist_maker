package com.example.playlist_maker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.search.usecase.HistoryInteractor
import com.example.playlist_maker.domain.search.usecase.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Default)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var searchJob: Job? = null

    private var currentText = ""
    private var lastQuery = ""
    private var hasFocus = false

    @Volatile
    private var requestId = 0


    fun onQueryChanged(text: String) {
        if (text == currentText) return
        currentText = text
        cancelDebounce()

        if (text.isBlank()) {
            requestId++
            showIdleState()
        } else {
            if (stateLiveData.value is SearchState.History) {
                stateLiveData.value = SearchState.Default
            }
            searchDebounce(text)
        }
    }

    fun onSearchSubmitted() {
        cancelDebounce()
        search(currentText)
    }

    fun onRetryClicked() = search(lastQuery)

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus = hasFocus
        if (currentText.isEmpty()) showIdleState()
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.addTrack(track)
        if (stateLiveData.value is SearchState.History) showIdleState()
    }

    fun onClearHistoryClicked() {
        historyInteractor.clearHistory()
        showIdleState()
    }



    private fun showIdleState() {
        val history = historyInteractor.getHistory()
        stateLiveData.value =
            if (hasFocus && currentText.isEmpty() && history.isNotEmpty()) {
                SearchState.History(history)
            } else {
                SearchState.Default
            }
    }

    private fun searchDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch{
            delay(SEARCH_DEBOUNCE_DELAY)
            search(query)
        }

    }

    private fun cancelDebounce() {
        searchJob?.cancel()
    }

    private fun search(query: String) {
        if (query.isBlank()) return
        stateLiveData.value = SearchState.Loading
        lastQuery = query
        viewModelScope.launch {
            tracksInteractor.search(query)
                .collect { pair ->
                    val tracks = pair.first
                    val errorMessage = pair.second
                    val newState = when{
                        tracks == null -> SearchState.Error
                        tracks.isEmpty() -> SearchState.Empty
                        else -> SearchState.Content(tracks)
                    }
                    stateLiveData.value = newState
                }
        }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}