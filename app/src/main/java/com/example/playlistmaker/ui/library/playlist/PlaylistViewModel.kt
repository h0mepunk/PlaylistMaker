package com.example.playlistmaker.ui.library.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    val playlistRepository: PlaylistRepository
): ViewModel() {
    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>()

    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsStateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistRepository.getPlaylists()
                .collect { playlists ->
                    processPlaylists(playlists)
                }
        }
    }

    private fun processPlaylists(playlists:List<Playlist>) {
        if (playlists.isEmpty()) {
            renderPlaylistsState(PlaylistsState.PlaylistsEmpty)
        } else {
            renderPlaylistsState(PlaylistsState.PlaylistsContent(playlists))
        }
    }

    private fun renderPlaylistsState(state: PlaylistsState) {
        playlistsStateLiveData.postValue(state)
    }
}