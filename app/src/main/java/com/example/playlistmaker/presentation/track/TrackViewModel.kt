package com.example.playlistmaker.presentation.track

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.db.LibraryInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(
    private val currentTrackInteractor: CurrentTrackInteractor,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val libraryInteractor: LibraryInteractor,
    private val mediaPlayer: MediaPlayer,
    private var playlistInteractor: PlaylistInteractor,
): ViewModel() {

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    var isFavorite: LiveData<Boolean> = isFavoriteLiveData
    private val stateLiveData = MutableLiveData<TrackState>()

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = playlistsLiveData

    private val trackAdded = MutableLiveData<Boolean>()
    val trackAddedToPlaylist: LiveData<Boolean> = trackAdded

    fun observeState(): LiveData<TrackState> = stateLiveData
    lateinit var currentTrack: Track

    private var timerJob: Job? = null

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlist.id)
        }

        if (playlist.tracks?.contains(currentTrack.trackId.toString()) == true) {
            Log.i(LOG_TAG,"track ${currentTrack.trackName} already exists in playlist with Id: ${playlist.name}")
            trackAdded.postValue(false)
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(
                    playlist.id,
                    currentTrack
                )
                playlistInteractor.insertTrack(currentTrack)
            }
            Log.i(
                LOG_TAG,
                "track ${currentTrack.trackName} added to playlist with Id: ${playlist.name}"
            )
            trackAdded.postValue(true)
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlistData ->
                    playlistsLiveData.postValue(playlistData)
                    if (playlistData.isNotEmpty()) {
                        Log.i(LOG_TAG, "Playlists loaded and posted to LiveData: $playlistData")
                    }
                }
        }
    }

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
            libraryInteractor.getTracks().collect { tracks ->
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
                libraryInteractor.deleteTrackFromFavorites(currentTrack)
                Log.i(LOG_TAG,"track removed from playlist: ${currentTrack.trackName}")
            } else {
                libraryInteractor.addTrackToFavorites(currentTrack)
                Log.i(LOG_TAG,"track added to playlist: ${currentTrack.trackName}")
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
            Log.i(LOG_TAG, "player completed")
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

    private fun trackTimeToMillis(trackTime: String): Long {
        val parts = trackTime.split(":")
        if (parts.size != 2) return 0
        val minutes = parts[0].toIntOrNull() ?: 0
        val seconds = parts[1].toIntOrNull() ?: 0
        return (minutes * 60 + seconds) * 1000L
    }

    companion object {
        private const val LOG_TAG = "TrackViewModel"
    }
}