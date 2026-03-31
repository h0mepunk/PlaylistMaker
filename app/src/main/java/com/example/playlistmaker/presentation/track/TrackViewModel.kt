package com.example.playlistmaker.presentation.track

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.db.LibraryInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.services.track.MusicService
import kotlinx.coroutines.launch

class TrackViewModel(
    private val currentTrackInteractor: CurrentTrackInteractor,
    private val libraryInteractor: LibraryInteractor,
    private var playlistInteractor: PlaylistInteractor,
): ViewModel() {

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    var isFavorite: LiveData<Boolean> = isFavoriteLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = playlistsLiveData

    private val trackAdded = MutableLiveData<Boolean>()
    val trackAddedToPlaylist: LiveData<Boolean> = trackAdded
    lateinit var currentTrack: Track

    private var musicService: MusicService? = null

    fun setMusicService(service: MusicService) {
        musicService = service
    }

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

    fun getIsTrackFavorite() {
        viewModelScope.launch {
            libraryInteractor.getTracks().collect { tracks ->
                val isFound = tracks.any { it.trackId == currentTrack.trackId }
                isFavoriteLiveData.postValue(isFound)
            }
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

    fun play() {
        musicService?.startPlayer()
    }

    fun pause() {
        musicService?.pausePlayer()
    }

    fun updateTimer() {
        musicService?.updateTimer()
    }

    fun showNotification() {
        musicService?.createServiceNotification()
    }

    fun onCreate() {
        currentTrack = currentTrackInteractor.getCurrentTrack()
        getIsTrackFavorite()
    }

    fun startForegroundMusicService(intent: Intent) {
        musicService?.startForeground(intent)
    }


    fun stopForegroundMusicService() {
        musicService?.stopForeground()
    }


    companion object {
        private const val LOG_TAG = "TrackViewModel"
    }
}