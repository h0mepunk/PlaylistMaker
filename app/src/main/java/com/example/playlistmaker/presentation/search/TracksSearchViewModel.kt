package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val trackHistoryInteractor: TracksHistoryInteractor,
): ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    var lastSearchText: String? = ""

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    fun onRestoreInstanceState(savedInstanceState: Bundle?): String? {
        val restored = savedInstanceState?.getCharSequence(SEARCH_TEXT)
        lastSearchText = restored?.toString() ?: EMPTY_SEARCH_TEXT
        return lastSearchText
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(SEARCH_TEXT, lastSearchText)
    }

    fun showHistory() {
        renderState(
            TracksState.History(trackHistoryInteractor.getTracksHistory())
        )
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        Log.i("TracksSearchViewModel", "foundTracks: $foundTracks")
        try {
            val tracks = mutableListOf<Track>()
            if (foundTracks != null) {
                Log.i("TracksSearchViewModel", tracks.toString())
                tracks.addAll(foundTracks)
            }
            when {
                errorMessage != null -> {
                    Log.i("TracksSearchViewModel", errorMessage)
                    renderState(TracksState.Error(errorMessage))
                    showToast.postValue(errorMessage)
                }
                tracks.isEmpty() -> {
                    Log.i("TracksSearchViewModel", "empty track list")
                    renderState(TracksState.Empty)
                }
                else -> {
                    Log.i("TracksSearchViewModel", tracks.toString())
                    renderState(TracksState.Content(tracks?: emptyList()))
                }
            }
        } catch (t: Throwable) {
            Log.i("TracksSearchViewModel", "Unexpected error")
            renderState(TracksState.UnknownErrorState)
            showToast.postValue(t.message ?: "Unexpected error")
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }
}