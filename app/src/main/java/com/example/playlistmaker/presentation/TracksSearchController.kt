package com.example.playlistmaker.presentation

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.util.Creator

class TracksSearchController(
    private val activity: Activity,
    private var adapter: TrackAdapter
) {
    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(activity)
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderIcon: ImageView
    private lateinit var placeholderMessage: View
    private lateinit var trackListRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var clearButton: ImageView
    private lateinit var clearTrackHistoryButton: Button
    private lateinit var searchHistoryTitle: TextView
    private lateinit var refreshButton: Button

    private lateinit var toolbar: Toolbar
    var trackList = ArrayList<Track>()
    var trackHistory = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    var textDump: CharSequence? = ""
    private lateinit var sharedPreferences : SharedPreferences

    private var trackHistoryInteractor : TracksHistoryInteractor = Creator.provideTracksHistoryInteractor()

    fun onCreate(savedInstanceState: Bundle?) {
        placeholderMessage = activity.findViewById(R.id.placeholderView)
        placeholderMessageText = activity.findViewById(R.id.placeholderMessageText)
        placeholderIcon = activity.findViewById(R.id.placeholderIcon)
        inputEditText = activity.findViewById(R.id.inputEditText)
        trackListRecycler = activity.findViewById(R.id.song_list_recycler)
        progressBar = activity.findViewById(R.id.searchProgressBar)
        clearButton =  activity.findViewById(R.id.clearIcon)
        clearTrackHistoryButton = activity.findViewById(R.id.clearHistoryButton)
        searchHistoryTitle = activity.findViewById(R.id.searchHistoryTitle)
        refreshButton = activity.findViewById(R.id.refreshButton)
        toolbar = activity.findViewById(R.id.search_toolbar)
        adapter = TrackAdapter()

        adapter.items = trackList

        trackListRecycler.layoutManager =
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
        trackListRecycler.adapter = adapter

        tracksInteractor = Creator.provideTracksInteractor(activity)
        sharedPreferences = activity.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()
        showHistory()
        placeholderMessage.visibility = View.GONE
        clearButton.isVisible = false
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            loadTracks(textDump.toString())
        }
        clearButton.setOnClickListener {
            inputEditText.setText(EMPTY_SEARCH_TEXT)
            showHistory()
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackList.clear()
            adapter.items = trackList
            adapter.notifyDataSetChanged()
        }
        toolbar.setNavigationOnClickListener {
            activity.finish()
        }
        clearTrackHistoryButton.setOnClickListener {
            trackHistory.clear()
            trackHistoryInteractor.saveTracksHistory(trackHistory)
            hideHistory()
        }

        inputEditText.setOnFocusChangeListener() { _, hasFocus -> }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    textDump = inputEditText.text
                    loadTracks(textDump.toString())
                }
            }
            false
        }
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textDump = s
                searchDebounce(textDump.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("TracksSearchController", "all callbacks removed")
                    handler.removeCallbacksAndMessages(null)
                    showHistory()
                }
                if((inputEditText.hasFocus()) && s.isNullOrEmpty()) {
                    showHistory()
                }
            }

        })

        if (savedInstanceState != null) {
            textDump = savedInstanceState.getCharSequence(
                SEARCH_TEXT,
                EMPTY_SEARCH_TEXT as CharSequence
            )
        }

    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        textDump = savedInstanceState.getCharSequence(
            SEARCH_TEXT,
            EMPTY_SEARCH_TEXT as CharSequence
        )
        inputEditText.setText(textDump)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(SEARCH_TEXT, textDump)
    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable(""))
    }

    private fun searchRunnable(text: String) = Runnable { loadTracks(text) }
    private fun searchDebounce(text: String,) {
        Log.e("TracksSearchController", "all callbacks removed")
        handler.removeCallbacksAndMessages(null)
        Log.e("TracksSearchController", "callback added $text")
        handler.postDelayed(searchRunnable(text), SEARCH_DEBOUNCE_DELAY)
    }

    fun loadTracks(text: String) {
        if (text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            hideHistory()
            trackListRecycler.visibility = View.GONE

            tracksInteractor.searchTracks(text, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("TracksSearchController", "foundTracks: $foundTracks")
                    if (foundTracks != null) {
                       activity.runOnUiThread {
                            progressBar.visibility = View.GONE
                            hideMessage()
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            adapter.items = trackList
                            adapter.notifyDataSetChanged()
                            trackListRecycler.visibility = View.VISIBLE
                            hideHistory()
                        }
                    }
                    if (errorMessage != null) {
                       activity.runOnUiThread {
                            showMessage(
                                text = R.string.network_error_text,
                                additionalMessage = errorMessage,
                                buttonVisibility = View.GONE,
                                icon = R.drawable.internet_error
                            )
                        }
                    } else if (foundTracks == null) {
                        activity.runOnUiThread {
                            showMessage(
                                text = R.string.empty_song_list_error_text,
                                additionalMessage = "",
                                buttonVisibility = View.GONE,
                                icon = R.drawable.empty_results_error
                            )
                        }
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
        buttonVisibility: Int = View.VISIBLE,
        icon: Int = R.drawable.internet_error
    ) {

        trackList.clear()
        adapter.items = trackList
        adapter.notifyDataSetChanged()
        hideHistory()
        if (text != null) {
            placeholderMessageText.text = getString(activity, text)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(activity, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }

            placeholderMessage.visibility = View.VISIBLE
            placeholderIcon.setBackgroundResource(icon)
            refreshButton.visibility = buttonVisibility

        } else {
            hideMessage()
        }
    }

    fun hideMessage() {
        placeholderMessage.visibility = View.GONE
    }

    fun hideHistory() {
        searchHistoryTitle.visibility = View.GONE
        clearTrackHistoryButton.visibility = View.GONE
    }

    fun showHistory() {
        trackHistory = trackHistoryInteractor.getTracksHistory()
        Log.e("TracksSearchController", trackHistory.toString())
        if (trackHistory.isNotEmpty()) {
            searchHistoryTitle.visibility = View.VISIBLE
            clearTrackHistoryButton.visibility = View.VISIBLE
            hideMessage()
            adapter.items = trackHistory
            adapter.notifyDataSetChanged()
            trackListRecycler.visibility = View.VISIBLE
        } else {
            hideHistory()
        }
    }
}