package com.example.playlistmaker.ui.library.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(
    val playlistCreateRepository: PlaylistCreateRepository
): ViewModel() {
    private val playlistCreateStateLiveData = androidx.lifecycle.MutableLiveData<PlaylistCreateState>()

    fun observePlaylistCreateState(): androidx.lifecycle.LiveData<PlaylistCreateState> = playlistCreateStateLiveData

    fun getPlaylist() {
        viewModelScope.launch {
            playlistCreateRepository.getCurrentPlaylist()
        }
    }

    private fun processPlaylist(playlist: Playlist) {
        if (playlist.name.isEmpty()) {
            renderPlaylistCreateState(PlaylistCreateState.PlaylistEmpty)
        } else {
            renderPlaylistCreateState(PlaylistCreateState.PlaylistContent(playlist))
        }
    }

    private fun renderPlaylistCreateState(state: PlaylistCreateState) {
        playlistCreateStateLiveData.postValue(state)
    }
}