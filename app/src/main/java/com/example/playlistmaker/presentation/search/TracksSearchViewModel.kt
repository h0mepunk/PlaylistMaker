package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import kotlin.toString

class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val trackHistoryInteractor: TracksHistoryInteractor,
): ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    val handler = Handler(Looper.getMainLooper())
    var lastSearchText: String? = ""

    private var latestSearchText: String? = null

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

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            tracksInteractor.searchTracks(newSearchText, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("TracksSearchViewModel", "foundTracks: $foundTracks")
                    try {
                        handler.post {
                            val tracks = mutableListOf<Track>()
                            if (foundTracks != null) {
                                Log.e("TracksSearchViewModel", tracks.toString())
                                tracks.addAll(foundTracks)
                            }
                            when {
                                errorMessage != null -> {
                                    Log.e("TracksSearchViewModel", errorMessage)
                                    renderState(TracksState.Error(errorMessage))
                                    showToast.postValue(errorMessage)
                                }
                                tracks.isEmpty() -> {
                                    Log.e("TracksSearchViewModel", "empty track list")
                                    renderState(TracksState.Empty)
                                }
                                else -> {
                                    Log.e("TracksSearchViewModel", tracks.toString())
                                    renderState(TracksState.Content(tracks?: emptyList()))
                                }
                            }
                        }
                    } catch (t: Throwable) {
                        Log.e("TracksSearchViewModel", "sww")
                        handler.post {
                            renderState(TracksState.UnknownErrorState)
                            showToast.postValue(t.message ?: "Unexpected error")
                        }
                    }
                }
            })
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}