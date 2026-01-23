package com.example.playlistmaker.presentation.library

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.track.TrackState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import kotlinx.coroutines.launch

class LibraryViewModel(
    val playlistRepository: PlaylistRepository
): ViewModel() {

    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>()

    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsStateLiveData

    private val tracksStateLiveData = MutableLiveData<TrackListState>()

    fun observeTracksState(): LiveData<TrackListState> = tracksStateLiveData

    fun getPlaylists(): LiveData<List<Playlist>> {
        val playlistsLiveData = MutableLiveData<List<Playlist>>()
        viewModelScope.launch {
            val playlists = playlistRepository.getPlaylists()
                .collect { playlists ->
                    processPlaylists(playlists)
            }
        }
        return playlistsLiveData
    }

    fun getCurrentTrackList() {
        viewModelScope.launch {
            playlistRepository.getTracks()
                .collect { tracks ->
                    processTracks(tracks)
                }
        }
    }

    private fun processTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderTrackListState(TrackListState.TracksEmpty)
        } else {
            renderTrackListState(TrackListState.TracksContent(tracks))
        }
    }

    private fun processPlaylists(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderPlaylistsState(PlaylistsState.PlaylistsEmpty)
        } else {
            renderPlaylistsState(PlaylistsState.PlaylistsContent(playlists))
        }
    }

    private fun renderTrackListState(state: TrackListState) {
        tracksStateLiveData.postValue(state)
    }

    private fun renderPlaylistsState(state: PlaylistsState) {
        playlistsStateLiveData.postValue(state)
    }
}