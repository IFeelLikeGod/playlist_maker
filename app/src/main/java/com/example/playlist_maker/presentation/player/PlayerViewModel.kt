package com.example.playlist_maker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.domain.player.usecase.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    previewUrl: String?,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (stateLiveData.value is PlayerState.Playing) {
                stateLiveData.value = PlayerState.Playing(currentProgress())
                handler.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    init {
        if (!previewUrl.isNullOrEmpty()) {
            playerInteractor.prepare(
                url = previewUrl,
                onPrepared = { stateLiveData.value = PlayerState.Prepared(START_TIME) },
                onCompletion = {
                    handler.removeCallbacks(timerRunnable)
                    stateLiveData.value = PlayerState.Prepared(START_TIME)
                }
            )
        }
    }

    fun onPlayButtonClicked() {
        when (stateLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Prepared, is PlayerState.Paused -> start()
            else -> Unit
        }
    }

    fun onPause() {
        if (stateLiveData.value is PlayerState.Playing) pause()
    }

    private fun start() {
        playerInteractor.start()
        stateLiveData.value = PlayerState.Playing(currentProgress())
        handler.post(timerRunnable)
    }

    private fun pause() {
        playerInteractor.pause()
        handler.removeCallbacks(timerRunnable)
        stateLiveData.value = PlayerState.Paused(currentProgress())
    }

    private fun currentProgress(): String =
        dateFormat.format(playerInteractor.getCurrentPosition())

    override fun onCleared() {
        handler.removeCallbacks(timerRunnable)
        playerInteractor.release()
    }

    companion object {
        private const val START_TIME = "00:00"
        private const val TIMER_DELAY = 300L

        fun getFactory(previewUrl: String?) = viewModelFactory {
            initializer {
                PlayerViewModel(previewUrl, Creator.providePlayerInteractor())
            }
        }
    }
}