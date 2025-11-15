package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.Const
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.main.App
import com.example.playlistmaker.util.Creator

class TracksSearchViewModel( private val context: Context): ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(context)
    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                TracksSearchViewModel(app)
            }
        }
    }
    val handler = Handler(Looper.getMainLooper())
    var lastSearchText: String? = ""

    private lateinit var sharedPreferences : SharedPreferences
    private var latestSearchText: String? = null

    var trackHistoryInteractor : TracksHistoryInteractor = Creator.provideTracksHistoryInteractor()

    fun onCreate() {
        tracksInteractor = Creator.provideTracksInteractor(context)
        sharedPreferences = context.getSharedPreferences(
            Const.PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()

    }

    fun onRestoreInstanceState(savedInstanceState: Bundle): String? {
        lastSearchText = savedInstanceState.getCharSequence(
            SEARCH_TEXT,
            EMPTY_SEARCH_TEXT as CharSequence
        ).toString()
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