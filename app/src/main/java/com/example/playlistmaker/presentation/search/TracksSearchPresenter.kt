package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.playlistmaker.Const
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Creator

class TracksSearchPresenter(
    val view: TracksSearchView,
    private val context: Context,
) {
    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(context)
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    var trackList = ArrayList<Track>()
    val handler = Handler(Looper.getMainLooper())
    var lastSearchText: String? = ""
    private lateinit var sharedPreferences : SharedPreferences

    var trackHistoryInteractor : TracksHistoryInteractor = Creator.provideTracksHistoryInteractor()

    fun onCreate() {
        view.updateTracksList(trackList)

        tracksInteractor = Creator.provideTracksInteractor(context)
        sharedPreferences = context.getSharedPreferences(
            Const.PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()

    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        lastSearchText = savedInstanceState.getCharSequence(
            SEARCH_TEXT,
            EMPTY_SEARCH_TEXT as CharSequence
        ).toString()
        view.setEditText(lastSearchText)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(SEARCH_TEXT, lastSearchText)
    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }
    fun searchDebounce(changedText: String) {
        Log.e("TracksSearchController", "all callbacks removed")
        this.lastSearchText = changedText
        handler.removeCallbacksAndMessages(null)
        Log.e("TracksSearchController", "callback added $changedText")
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            view.showLoading()
            tracksInteractor.searchTracks(newSearchText, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("TracksSearchController", "foundTracks: $foundTracks")
                    handler.post {
                        if (foundTracks != null) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            view.showContent(trackList)
                        }
                        if (errorMessage != null) {
                            view.showError(errorMessage)
                        } else if (foundTracks.isNullOrEmpty()) {
                            view.showEmpty(errorMessage)
                        } else {
                            view.showPlaceholderMessage(false)
                        }
                    }
                }
                // Add error response handling later
            })

//                onErrorResponse = {
//                    errorText ->
//                    progressBar.visibility = View.GONE
//                    showMessage(
//                        text = R.string.network_error_text,
//                        additionalMessage = errorText,
//                        buttonVisibility = View.VISIBLE,
//                        icon = R.drawable.internet_error
//                    )
//                },
//                onError = { t ->
//                    progressBar.visibility = View.GONE
//                    t.printStackTrace()
//                showMessage(
//                    text = R.string.network_error_text,
//                    additionalMessage = "",
//                    buttonVisibility = View.VISIBLE,
//                    icon = R.drawable.internet_error
//                )
//                }
//            )
        }
    }

    fun getHistory(): List<Track> {
        return trackHistoryInteractor.getTracksHistory()
    }
}