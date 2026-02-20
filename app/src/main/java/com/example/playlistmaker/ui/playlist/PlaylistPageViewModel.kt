package com.example.playlistmaker.ui.playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.PlaylistPageState
import kotlinx.coroutines.launch

class PlaylistPageViewModel(
    val playlistInteractor: PlaylistInteractor,
    val  playlistCreateInteractor: PlaylistCreateInteractor,
): ViewModel() {

    companion object {
        private const val LOG_TAG = "PlaylistPageViewModel"
    }

    private val tracksStateLiveData = MutableLiveData< PlaylistPageState>()

    fun observeTracksState(): LiveData<PlaylistPageState> = tracksStateLiveData

    fun getTracks() {
        val playlist = playlistCreateInteractor.getCurrentPlaylist()
        if (playlist == null) {
            Log.i(LOG_TAG, "getTracks: playlist is null")
            renderPlaylistPageState(PlaylistPageState.Empty)
            return
        }
        viewModelScope.launch {
            playlistInteractor.getTracksFromPlaylist(playlist.id)
                .collect { tracks ->
                    processTracks(tracks)
                }
        }
    }

    private fun processTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderPlaylistPageState(PlaylistPageState.Empty)
        } else {
            renderPlaylistPageState(PlaylistPageState.Tracks(tracks))
        }
    }

    private fun renderPlaylistPageState(state: PlaylistPageState) {
        tracksStateLiveData.postValue(state)
    }
}