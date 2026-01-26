package com.example.playlistmaker.ui.library.tracklist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.TrackListState
import kotlinx.coroutines.launch

class TrackListViewModel(private val libraryRepository: LibraryRepository): ViewModel() {

    private val tracksStateLiveData = MutableLiveData<TrackListState>()

    fun observeTracksState(): LiveData<TrackListState> = tracksStateLiveData


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
            Log.i("TrackListFragment","TracksEmpty state")
            renderTrackListState(TrackListState.TracksEmpty)
        } else {
            Log.i("TrackListFragment","TracksContent state with ${tracks.size} tracks")
            renderTrackListState(TrackListState.TracksContent(tracks))
        }
    }


    private fun renderTrackListState(state: TrackListState) {
        tracksStateLiveData.postValue(state)
    }

}