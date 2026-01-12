package com.example.playlistmaker.presentation.track

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor,
    private val mediaPlayer: MediaPlayer
): ViewModel() {

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observeState(): LiveData<TrackState> = stateLiveData
    lateinit var currentTrack: Track
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

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

    fun onPlayButtonClicked() {
        when(stateLiveData.value) {
            is TrackState.Playing -> {
                pausePlayer()
            }
            is TrackState.Prepared, is TrackState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
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