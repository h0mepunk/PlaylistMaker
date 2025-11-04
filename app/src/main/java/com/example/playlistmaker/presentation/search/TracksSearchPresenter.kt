package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.playlistmaker.Const
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.model.TracksState
import com.example.playlistmaker.util.Creator

class TracksSearchPresenter(
    private val context: Context,
) {
    private var view: TracksSearchView? = null
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
            view?.render(TracksState.Loading)
            tracksInteractor.searchTracks(newSearchText, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("TracksSearchController", "foundTracks: $foundTracks")
                    handler.post {
                        if (foundTracks != null) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            view?.render(TracksState.Content(trackList))
                        }
                        if (errorMessage != null) {
                            view?.render(
                                TracksState.Error(errorMessage)
                            )
                            view?.showToast(errorMessage)
                        } else if (foundTracks.isNullOrEmpty()) {
                            view?.render(TracksState.Empty)
                        } else {
                            view?.render(
                                TracksState.UnknownErrorState
                            )
                        }
                    }
                }
            })
        }
    }

    fun getHistory(): List<Track> {
        return trackHistoryInteractor.getTracksHistory()
    }

    fun attachView(view: TracksSearchView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

}