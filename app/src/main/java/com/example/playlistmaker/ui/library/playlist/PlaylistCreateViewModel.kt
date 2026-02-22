package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.Const.EMPTY_STRING
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(
    val playlistCreateInteractor: PlaylistCreateInteractor,
    val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private val playlistCreateStateLiveData = MutableLiveData<PlaylistCreateState>()

    fun observePlaylistCreateState(): LiveData<PlaylistCreateState> = playlistCreateStateLiveData

    private val playlistLiveData = MutableLiveData<Playlist>()

    fun observePlaylistLiveData(): LiveData<Playlist> = playlistLiveData

    companion object {
        private const val PLAYLIST_NAME = "playlist_name"
        private const val PLAYLIST_DESCRIPTION = "playlist_description"
        private const val COVER_URI = "cover_uri"
        private const val LOG_TAG = "PlaylistCreateViewModel"
    }

    var isEdited: Boolean? = null
    var playlistName: String = ""
    var playlistDescription: String = ""
    var coverUri: String = ""
    var playlist: Playlist? = null

    fun getPlaylist() {
        viewModelScope.launch {
            val playlist = playlistCreateInteractor.getCurrentCreatingPlaylist()
            Log.i(LOG_TAG, "playlist got $playlist")
            processPlaylist(playlist)
        }
    }

    fun getIsEditedFlag() {
        isEdited = playlistCreateInteractor.getIsEditedFlag()
        Log.i(LOG_TAG, "is edited flag got $isEdited")
    }

    fun setIsEditedFlag(isEdited: Boolean) {
        playlistCreateInteractor.setIsEditedFlag(isEdited)
        Log.i(LOG_TAG, "is edited flag saved $isEdited")
    }

    fun savePlaylist(
        coverUri: String = EMPTY_STRING,
        playlistName: String,
        playlistDescription: String
    ) {
        Log.i(LOG_TAG, "savePlaylist called with name $playlistName and description $playlistDescription and coverUri $coverUri")
        viewModelScope.launch {
            if (isEdited == true) {
                Log.i(LOG_TAG, "updating existing playlist: $playlist with new name $playlistName and description $playlistDescription and coverUri $coverUri")
                playlist = playlist?.copy(
                    name = playlistName,
                    description = playlistDescription,
                    imgUri = coverUri
                )
                playlistInteractor.updatePlaylist(playlist!!)
                playlistCreateInteractor.saveCurrentPlaylist(playlist!!)
            } else {
                playlistInteractor.insertPlaylist(
                    Playlist(
                        name = playlistName,
                        description = playlistDescription,
                        tracks = EMPTY_STRING,
                        tracksCount = 0,
                        id = (1..1000000000).random(),
                        imgUri = coverUri ?: EMPTY_STRING,
                        timestamp = System.currentTimeMillis(),
                        timeTotal = 0L
                    )
                )
            }
        }
    }

    fun saveCurrentPlaylist() {
        playlist = playlistCreateInteractor.getCurrentCreatingPlaylist()
        playlistCreateInteractor.saveCurrentPlaylist(playlist)
    }

    fun clearCurrentCreatingPlaylist() {
        playlistCreateInteractor.saveCurrentCreatingPlaylist(null)
    }

    fun onSaveInstanceState(outState: Bundle) {
        playlist = playlistCreateInteractor.getCurrentCreatingPlaylist()
        outState.putString(PLAYLIST_NAME, playlist?.name?: EMPTY_STRING)
        outState.putString(PLAYLIST_DESCRIPTION, playlist?.description?: EMPTY_STRING)
        outState.putString(COVER_URI, coverUri)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        coverUri = savedInstanceState?.getString(COVER_URI)?: EMPTY_STRING
        playlist = playlistCreateInteractor.getCurrentCreatingPlaylist()
        processPlaylist(playlist)
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