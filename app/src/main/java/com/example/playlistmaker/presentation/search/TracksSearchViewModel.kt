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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val trackHistoryInteractor: TracksHistoryInteractor,
): ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private val _trackList = MutableStateFlow<List<Track>>(emptyList())
    val trackList: StateFlow<List<Track>> = _trackList.asStateFlow()

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _track = MutableStateFlow<Track?>(null)
    val track: StateFlow<Track?> = _track.asStateFlow()

    private val _historyTitleVisible = MutableStateFlow(false)
    val historyTitleVisible: StateFlow<Boolean> = _historyTitleVisible.asStateFlow()

    private val _errorVisible = MutableStateFlow(false)
    val errorVisible: StateFlow<Boolean> = _errorVisible.asStateFlow()

    private val _recyclerVisible = MutableStateFlow(false)
    val recyclerVisible: StateFlow<Boolean> = _recyclerVisible.asStateFlow()

    private val _progressBarVisible = MutableStateFlow(false)
    val progressBarVisible: StateFlow<Boolean> = _progressBarVisible.asStateFlow()

    private val _clearHistoryVisible = MutableStateFlow(false)
    val clearHistoryVisible: StateFlow<Boolean> = _clearHistoryVisible.asStateFlow()

    private val _refreshButtonVisible = MutableStateFlow(false)
    val refreshButtonVisible: StateFlow<Boolean> = _refreshButtonVisible.asStateFlow()

    private val _clearIconVisibility = MutableStateFlow(false)
    val clearIconVisibility: StateFlow<Boolean> = _clearIconVisibility.asStateFlow()

    private val _hideKeyboard = MutableStateFlow(false)
    val hideKeyboard: StateFlow<Boolean> = _hideKeyboard.asStateFlow()

    private val _errorText = MutableStateFlow("")
    val errorText: StateFlow<String> = _errorText.asStateFlow()

    private val _errorIcon = MutableStateFlow(0)
    val errorIcon: StateFlow<Int> = _errorIcon.asStateFlow()

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val LOG_TAG = "TrackSearchViewModel"
    }
    var lastSearchText: String? = ""

    private var latestSearchText: String? = null

    private var tracksHistory: List<Track> = emptyList()

    private var searchJob: Job? = null


    fun setClearIconVisibility(visible: Boolean) {
        _clearIconVisibility.value = visible
    }

    fun setErrorVisibility(visible: Boolean) {
        _errorVisible.value = visible
    }


    fun setHistoryTitleVisibility(visible: Boolean) {
        _historyTitleVisible.value = visible
    }

    fun setClearHistoryButtonVisibility(visible: Boolean) {
        _clearHistoryVisible.value = visible
    }

    fun setText(text: String) {
        _text.value = text
        setClearIconVisibility(text.isNotEmpty())
    }

    fun setErrorMessageText(text: String) {
        _errorText.value = text
    }

    fun setErrorIconResource(icon: Int) {
        _errorIcon.value = icon
    }
    fun setTrackList(tracks: List<Track>) {
        _trackList.value = tracks
    }
    fun onTextChanged(newText: String) {
        _text.value = newText
        searchDebounce(newText)
    }

    fun onItemClick(track: Track) {
        _track.value = track
    }

    fun applyVisibility(
        placeholderVisible: Boolean,
        recyclerVisible: Boolean,
        progressBarVisible: Boolean,
        historyTitleVisible: Boolean,
        clearHistoryVisible: Boolean,
        refreshButtonVisible: Boolean = false
    ) {
        _errorVisible.value = placeholderVisible
        _historyTitleVisible.value = historyTitleVisible
        _recyclerVisible.value = recyclerVisible
        _progressBarVisible.value = progressBarVisible
        _clearHistoryVisible.value = clearHistoryVisible
        _refreshButtonVisible.value = refreshButtonVisible
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?): String? {
        val restored = savedInstanceState?.getCharSequence(SEARCH_TEXT)
        tracksHistory = trackHistoryInteractor.getTracksHistory()
        if (tracksHistory.isNotEmpty()) {
            renderState(TracksState.History(tracksHistory))
        } else {
            renderState(TracksState.Initial)
        }
        lastSearchText = restored?.toString() ?: EMPTY_SEARCH_TEXT
        _text.value = lastSearchText ?: EMPTY_SEARCH_TEXT//?
        return lastSearchText
    }

    fun onSaveInstanceState(outState: Bundle) {
        tracksHistory = trackHistoryInteractor.getTracksHistory()
        outState.putCharSequence(SEARCH_TEXT, lastSearchText)
    }

    fun hideKeyboard(){
        _hideKeyboard.value = true
    }

    fun showHistory() {
        Log.i(LOG_TAG, "showHistory called")
        tracksHistory = trackHistoryInteractor.getTracksHistory()
        renderState(
            TracksState.History(tracksHistory)
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
        Log.i(LOG_TAG, "foundTracks: $foundTracks")
        try {
            val tracks = mutableListOf<Track>()
            if (foundTracks != null) {
                Log.i(LOG_TAG, foundTracks.toString())
                tracks.addAll(foundTracks)
            }
            when {
                errorMessage != null -> {
                    Log.i(LOG_TAG, errorMessage)
                    renderState(TracksState.Error(errorMessage))
                    showToast.postValue(errorMessage)
                }
                tracks.isEmpty() -> {
                    Log.i(LOG_TAG, "empty track list")
                    renderState(TracksState.Empty)
                }
                else -> {
                    Log.i(LOG_TAG, tracks.toString())
                    renderState(TracksState.Content(tracks))
                }
            }
        } catch (t: Throwable) {
            Log.i(LOG_TAG, "Unexpected error")
            renderState(TracksState.UnknownErrorState)
            showToast.postValue(t.message ?: "Unexpected error")
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }
}