package com.example.playlistmaker.presentation.library

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.library.error.ErrorFragment

class LibraryViewModel: ViewModel() {

    fun setCurrentPlaylist(playlistList: List<Playlist>) {
        //TODO
    }

    fun getCurrentPlaylist(): LiveData<List<Playlist>> {
        //TODO
        return MutableLiveData(emptyList())
    }

    fun setCurrentTrackList(trackList: List<Track>) {
        //TODO
    }

    fun getCurrentTrackList():  LiveData<List<Track>> {
        //TODO
        return MutableLiveData(emptyList())
    }
}