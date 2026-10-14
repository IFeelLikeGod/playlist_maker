package com.example.playlist_maker.presentation.search

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.search.usecase.HistoryInteractor
import com.example.playlist_maker.domain.search.usecase.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Default)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var currentText = ""
    private var lastQuery = ""
    private var hasFocus = false

    @Volatile
    private var requestId = 0

    // ---------- действия пользователя ----------

    fun onQueryChanged(text: String) {
        if (text == currentText) return   // например, после поворота экрана
        currentText = text
        cancelDebounce()

        if (text.isBlank()) {
            requestId++                   // ответ на старый запрос больше не нужен
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

    // ---------- внутренняя кухня ----------

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
        val runnable = Runnable { search(query) }
        searchRunnable = runnable
        handler.postDelayed(runnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun cancelDebounce() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = null
    }

    private fun search(query: String) {
        if (query.isBlank()) return

        lastQuery = query
        val id = ++requestId
        stateLiveData.value = SearchState.Loading

        tracksInteractor.search(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, errorMessage: String?) {
                if (id != requestId) return   // пришёл устаревший ответ
                val newState = when {
                    errorMessage != null -> SearchState.Error
                    foundTracks.isEmpty() -> SearchState.Empty
                    else -> SearchState.Content(foundTracks)
                }
                stateLiveData.postValue(newState)
            }
        })
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}