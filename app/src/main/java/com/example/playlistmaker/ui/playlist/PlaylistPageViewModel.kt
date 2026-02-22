package com.example.playlistmaker.ui.playlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.PlaylistPageState
import com.example.playlistmaker.ui.track.TrackFragment
import kotlinx.coroutines.launch

class PlaylistPageViewModel(
    val playlistInteractor: PlaylistInteractor,
    val  playlistCreateInteractor: PlaylistCreateInteractor,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "PlaylistPageViewModel"
    }

    lateinit var currentPlaylist: Playlist

    var trackList: List<Track> = emptyList()

    private val tracksStateLiveData = MutableLiveData< PlaylistPageState>()

    fun observeTracksState(): LiveData<PlaylistPageState> = tracksStateLiveData

    fun onCreate() {
        getCurrentPlaylist()
        getTracks()
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(currentPlaylist.id, track)
        }
    }

    fun getCurrentPlaylist() {
        val playlist = playlistCreateInteractor.getCurrentPlaylist()
        if (playlist == null) {
            Log.i(LOG_TAG, "getCurrentPlaylist: playlist is null")
            renderPlaylistPageState(PlaylistPageState.Empty)
        } else {
            currentPlaylist = playlist
        }
    }

    fun getTracks() {
        viewModelScope.launch {
            playlistInteractor.getTracksFromPlaylist(currentPlaylist.id)
                .collect { tracks ->
                    processTracks(tracks)
                    Log.i(LOG_TAG, "getTracks: tracks received ${tracks.size} for playlist ${currentPlaylist.name}")
                    trackList = tracks
                }
        }
    }

    private fun processTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderPlaylistPageState(PlaylistPageState.Empty)
        } else {
            renderPlaylistPageState(PlaylistPageState.Tracks(currentPlaylist, tracks))
        }
    }

    private fun renderPlaylistPageState(state: PlaylistPageState) {
        tracksStateLiveData.postValue(state)
    }
}