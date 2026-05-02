package com.example.playlistmaker.ui.library.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>()

    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsStateLiveData

    private val _errorVisible = MutableStateFlow(false)
    val errorVisible: StateFlow<Boolean> = _errorVisible.asStateFlow()

    private val _playlistsList = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistsList: StateFlow<List<Playlist>> = _playlistsList.asStateFlow()

    fun setPlaylistsList(playlists: List<Playlist>) {
        _playlistsList.value = playlists
    }

    fun setErrorVisibility(visible: Boolean) {
        _errorVisible.value = visible
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
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