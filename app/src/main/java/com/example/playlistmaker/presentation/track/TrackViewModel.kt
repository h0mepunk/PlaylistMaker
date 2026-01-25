package com.example.playlistmaker.presentation.track

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(
    private val currentTrackInteractor: CurrentTrackInteractor,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val mediaPlayer: MediaPlayer
): ViewModel() {

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    var isFavorite: LiveData<Boolean> = isFavoriteLiveData
    private val stateLiveData = MutableLiveData<TrackState>()
    fun observeState(): LiveData<TrackState> = stateLiveData
    lateinit var currentTrack: Track

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(250L)
                stateLiveData.postValue(TrackState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    fun getIsTrackFavorite() {
        viewModelScope.launch {
            playlistInteractor.getTracks().collect { tracks ->
                val isFound = tracks.any { it.trackId == currentTrack.trackId }
                isFavoriteLiveData.postValue(isFound)
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
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
                startTimer()
            }
        )
    }

    fun pausePlayer(trackTime: String) {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                renderState(TrackState.Paused(trackTime))
                timerJob?.cancel()
            }
        )
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer(
            onStop = {
                renderState(TrackState.Stopped)
                timerJob?.cancel()
            }
        )
    }

    fun onPlayButtonClicked(trackTime: String) {
        when(stateLiveData.value) {
            is TrackState.Playing -> {
                pausePlayer(trackTime)
            }
            is TrackState.Prepared, is TrackState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    fun onLikeButtonClicked() {
        val currentlyFavorite = isFavorite.value ?: false
        viewModelScope.launch {
            if (currentlyFavorite) {
                playlistInteractor.removeTrackFromPlaylist(currentTrack)
                Log.i("TrackViewModel","track removed from playlist: ${currentTrack.trackName}")
            } else {
                playlistInteractor.addTrackToPlaylist(currentTrack)
                Log.i("TrackViewModel","track added to playlist: ${currentTrack.trackName}")
            }
            isFavoriteLiveData.postValue(!currentlyFavorite)
        }
    }

    fun onCreate() {
        currentTrack = currentTrackInteractor.getCurrentTrack()

        getIsTrackFavorite()

        preparePlayer(currentTrack.previewUrl)

        renderState(
            TrackState.Init(
                currentTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            )
        )

        mediaPlayer.setOnCompletionListener {
            Log.i("TrackController", "player completed")
            stopPlayer()
        }
    }

    private fun renderState(state: TrackState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.reset()
    }
}