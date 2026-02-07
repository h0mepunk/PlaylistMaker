package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.Const.EMPTY_STRING
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(
    val playlistCreateRepository: PlaylistCreateRepository
): ViewModel() {
    private val playlistCreateStateLiveData = androidx.lifecycle.MutableLiveData<PlaylistCreateState>()

    fun observePlaylistCreateState(): androidx.lifecycle.LiveData<PlaylistCreateState> = playlistCreateStateLiveData

    companion object {
        private const val PLAYLIST_NAME = "playlist_name"
        private const val PLAYLIST_DESCRIPTION = "playlist_description"
        private const val LOG_TAG = "PlaylistCreateViewModel"
    }

    var playlistName: String = ""

    var playlistDescription: String = ""

    private var playlist: Playlist? = null

    fun getPlaylist() {
        viewModelScope.launch {
            playlistCreateRepository.getCurrentPlaylist()
        }
    }



    fun onSaveInstanceState(outState: Bundle) {
        playlist = playlistCreateRepository.getCurrentPlaylist()
        outState.putString(PLAYLIST_NAME, playlist?.name?: EMPTY_STRING)
        outState.putString(PLAYLIST_DESCRIPTION, playlist?.description?: EMPTY_STRING)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?): String {
        val restoredName = savedInstanceState?.getCharSequence(PLAYLIST_NAME)?.toString() ?: EMPTY_STRING
        val restoredDescription = savedInstanceState?.getCharSequence(PLAYLIST_DESCRIPTION)?.toString() ?: EMPTY_STRING
        playlist = playlistCreateRepository.getCurrentPlaylist()
        processPlaylist(playlist)
        return "$restoredName,$restoredDescription"
    }

    private fun processPlaylist(playlist: Playlist?) {
        if (playlist == null) {
            Log.i(LOG_TAG, "playlist is null")
            renderPlaylistCreateState(PlaylistCreateState.PlaylistEmpty)
        } else {
            Log.i(LOG_TAG, "playlist got $playlist")
            renderPlaylistCreateState(PlaylistCreateState.PlaylistContent(playlist))
        }
    }

    private fun renderPlaylistCreateState(state: PlaylistCreateState) {
        playlistCreateStateLiveData.postValue(state)
    }
}