
package com.example.playlist_maker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.db.FavoritesInteractor
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.player.usecase.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    previewUrl: String?,
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    // Состояние аудиоплеера
    private val stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)

    fun observeState(): LiveData<PlayerState> = stateLiveData

    // Состояние избранного
    private val favoriteLiveData = MutableLiveData(false)

    fun observeFavorite(): LiveData<Boolean> = favoriteLiveData

    private var timerJob: Job? = null
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    init {
        // Получаем актуальное состояние избранного из Room
        viewModelScope.launch {
            favoritesInteractor.isFavorite(track.trackId).collect { isFavorite ->
                favoriteLiveData.value = isFavorite
            }
        }

        // Подготавливаем аудиоплеер
        if (!previewUrl.isNullOrEmpty()) {
            playerInteractor.prepare(
                url = previewUrl,
                onPrepared = {
                    stateLiveData.value = PlayerState.Prepared(START_TIME)
                },
                onCompletion = {
                    timerJob?.cancel()
                    stateLiveData.value = PlayerState.Prepared(START_TIME)
                }
            )
        }
    }

    // Добавление или удаление трека из избранного
    fun onFavoriteClicked() {
        viewModelScope.launch {
            val isFavorite = favoriteLiveData.value ?: false

            if (isFavorite) {
                favoritesInteractor.deleteTrack(track)
            } else {
                favoritesInteractor.addTrack(track)
            }

            favoriteLiveData.value = !isFavorite
        }
    }

    // Управление воспроизведением
    fun onPlayButtonClicked() {
        when (stateLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Prepared, is PlayerState.Paused -> start()
            else -> Unit
        }
    }

    fun onPause() {
        if (stateLiveData.value is PlayerState.Playing) {
            pause()
        }
    }

    private fun start() {
        playerInteractor.start()
        stateLiveData.value = PlayerState.Playing(currentProgress())
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (stateLiveData.value is PlayerState.Playing) {
                delay(TIMER_DELAY)
                stateLiveData.value = PlayerState.Playing(currentProgress())
            }
        }
    }

    private fun pause() {
        playerInteractor.pause()
        timerJob?.cancel()
        stateLiveData.value = PlayerState.Paused(currentProgress())
    }

    private fun currentProgress(): String =
        dateFormat.format(playerInteractor.getCurrentPosition())

    override fun onCleared() {
        timerJob?.cancel()
        playerInteractor.release()
        super.onCleared()
    }

    companion object {
        private const val START_TIME = "00:00"
        private const val TIMER_DELAY = 300L
    }
}
