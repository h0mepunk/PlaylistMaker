package com.example.playlistmaker.presentation.track

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.main.App
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(): ViewModel() {

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observeState(): LiveData<TrackState> = stateLiveData
    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
    private val mediaPlayer = Creator.provideMediaPlayerInteractor().getMediaPlayer()
    lateinit var currentTrack: Track
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                TrackViewModel()
            }
        }
    }

    private fun updateTimer() {
        mediaPlayerInteractor.updateTimer(
            onUpdate = {
                renderState(
                    TrackState.Playing(
                        SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                    )
                )
                handler.postDelayed(timerRunnable, 250)
            }
        )
    }

    private fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url,
            onPrepared = {
                renderState(TrackState.Prepared)
            },
            onCompletion = {
                renderState(TrackState.Playing(null))
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer(
            onPlaying = {
                renderState(TrackState.Playing(null))
                handler.post(timerRunnable)
            }
        )
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                renderState(TrackState.Paused)
                handler.removeCallbacks(timerRunnable)
            }
        )
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer(
            onStop = {
                renderState(TrackState.Stopped)
            }
        )
    }

    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(
            start = {
                startPlayer()
            },
            pause = {
                pausePlayer()
            }
        )
    }

    fun onCreate() {
        currentTrack = tracksHistoryInteractor.getCurrentTrack()

        renderState(
            TrackState.Init(
                currentTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            )
        )

        mediaPlayer.setOnCompletionListener {
            Log.e("TrackController", "player completed")
            stopPlayer()
        }

        preparePlayer(currentTrack.previewUrl)
    }

    private fun renderState(state: TrackState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.reset()
    }
}