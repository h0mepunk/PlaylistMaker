package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.playlistmaker.Const
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.util.Creator

class TracksSearchPresenter(
    val view: TracksSearchView,
    private val context: Context,
    private var adapter: TrackAdapter
) {
    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(context)
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    var trackList = ArrayList<Track>()
    var trackHistory = ArrayList<Track>()
    val handler = Handler(Looper.getMainLooper())
    var lastSearchText: String? = ""
    private lateinit var sharedPreferences : SharedPreferences

    var trackHistoryInteractor : TracksHistoryInteractor = Creator.provideTracksHistoryInteractor()

    fun onCreate(savedInstanceState: Bundle?) {
       // adapter = TrackAdapter()

        adapter.items = trackList

//        trackListRecycler.layoutManager =
//            LinearLayoutManager(
//                activity,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//        trackListRecycler.adapter = adapter

        tracksInteractor = Creator.provideTracksInteractor(context)
        sharedPreferences = context.getSharedPreferences(
            Const.PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()
        showHistory()
//        placeholderMessage.visibility = View.GONE
//        clearButton.isVisible = false
//        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//
//        refreshButton.setOnClickListener {
//            progressBar.visibility = View.VISIBLE
//            loadTracks(textDump.toString())
//        }
//        clearButton.setOnClickListener {
//            inputEditText.setText(EMPTY_SEARCH_TEXT)
//            showHistory()
//            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
//            trackList.clear()
//            adapter.items = trackList
//            adapter.notifyDataSetChanged()
//        }
//        toolbar.setNavigationOnClickListener {
//            activity.finish()
//        }
//        clearTrackHistoryButton.setOnClickListener {
//            trackHistory.clear()
//            trackHistoryInteractor.saveTracksHistory(trackHistory)
//            hideHistory()
//        }
//
//        inputEditText.setOnFocusChangeListener() { _, hasFocus -> }
//        inputEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (inputEditText.text.isNotEmpty()) {
//                    inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
//                    textDump = inputEditText.text
//                    loadTracks(textDump.toString())
//                }
//            }
//            false
//        }
//        inputEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                textDump = s
//                searchDebounce(textDump.toString())
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.isNullOrEmpty()) {
//                    Log.e("TracksSearchController", "all callbacks removed")
//                    handler.removeCallbacksAndMessages(null)
//                    showHistory()
//                }
//                if((inputEditText.hasFocus()) && s.isNullOrEmpty()) {
//                    showHistory()
//                }
//            }
//
//        })
//
        if (savedInstanceState != null) {
            lastSearchText = savedInstanceState.getCharSequence(
                SEARCH_TEXT,
                EMPTY_SEARCH_TEXT as CharSequence
            ).toString()
        }

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
            view.showProgressBar(true)
            view.showHistory(false)
            view.showTracksList(false)

            tracksInteractor.searchTracks(newSearchText, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("TracksSearchController", "foundTracks: $foundTracks")
                    if (foundTracks != null) {
                       //activity.runOnUiThread {
                            view.showProgressBar(false)
                            hideMessage()
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            adapter.items = trackList
                            adapter.notifyDataSetChanged()
                            view.showTracksList(true)//trackListRecycler.visibility = View.VISIBLE
                            hideHistory()
                     //   }
                    }
                    if (errorMessage != null) {
                     //  context.runOnUiThread {
                            showMessage(
                                text = R.string.network_error_text,
                                additionalMessage = errorMessage,
                                isButtonVisible = false,
                                icon = R.drawable.internet_error
                            )
                     //   }
                    } else if (foundTracks == null) {
                       // activity.runOnUiThread {
                            showMessage(
                                text = R.string.empty_song_list_error_text,
                                additionalMessage = "",
                                isButtonVisible = false,
                                icon = R.drawable.empty_results_error
                            )
                     //   }
                    } else {
                        hideMessage()
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

    private fun showMessage(
        text: Int?,
        additionalMessage: String,
        isButtonVisible: Boolean = true,
        icon: Int = R.drawable.internet_error
    ) {

        trackList.clear()
        adapter.items = trackList
        adapter.notifyDataSetChanged()
        hideHistory()
        if (text != null) {
            view.changePlaceholderMessage(ContextCompat.getString(context, text))
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(context, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }

            view.showPlaceholderMessage(true)
            view.setPlaceholderIcon(icon)
            view.showRefreshButton(isButtonVisible)

        } else {
            hideMessage()
        }
    }

    fun hideMessage() {
        view.showPlaceholderMessage(false)
    }

    fun hideHistory() {
        view.showHistory(false)
    }

    fun showHistory() {
        trackHistory = trackHistoryInteractor.getTracksHistory()
        Log.e("TracksSearchController", trackHistory.toString())
        if (trackHistory.isNotEmpty()) {
            view.showHistory(true)
            hideMessage()
            adapter.items = trackHistory
            adapter.notifyDataSetChanged()
            view.showTracksList(true)
        } else {
            hideHistory()
        }
    }
}