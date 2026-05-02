package com.example.playlistmaker.ui.library.tracklist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.TrackListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackListViewModel(private val libraryRepository: LibraryRepository): ViewModel() {

    private val tracksStateLiveData = MutableLiveData<TrackListState>()

    fun observeTracksState(): LiveData<TrackListState> = tracksStateLiveData

    private val _trackList = MutableStateFlow<List<Track>>(emptyList())
    val trackList: StateFlow<List<Track>> = _trackList.asStateFlow()

    fun setTrackList(tracks: List<Track>) {
        _trackList.value = tracks
    }

    private val _errorVisible = MutableStateFlow(false)
    val errorVisible: StateFlow<Boolean> = _errorVisible.asStateFlow()

    fun setErrorVisibility(visible: Boolean) {
        _errorVisible.value = visible
    }


    fun getTrackList() {
        viewModelScope.launch {
            libraryRepository.getTracks()
                .collect { tracks ->
                    processTracks(tracks)
                }
        }
    }

    private fun processTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            Log.i(LOG_TAG,"TracksEmpty state")
            renderTrackListState(TrackListState.TracksEmpty)
        } else {
            Log.i(LOG_TAG,"TracksContent state with ${tracks.size} tracks")
            renderTrackListState(TrackListState.TracksContent(tracks))
        }
    }


    private fun renderTrackListState(state: TrackListState) {
        tracksStateLiveData.postValue(state)
    }

    companion object {
        private const val LOG_TAG = "TrackListViewModel"
    }

}