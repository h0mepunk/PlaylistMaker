package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class LibraryViewModel(
    val playlistRepository: PlaylistRepository
): ViewModel() {

    val playlistsLiveData = MutableLiveData<List<Playlist>>()
    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>()

    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsStateLiveData

    private val tracksStateLiveData = MutableLiveData<TrackListState>()

    fun observeTracksState(): LiveData<TrackListState> = tracksStateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
             playlistRepository.getPlaylists()
                .collect { playlists ->
                    processPlaylists(playlists)
            }
        }
    }

    fun getTrackList() {
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