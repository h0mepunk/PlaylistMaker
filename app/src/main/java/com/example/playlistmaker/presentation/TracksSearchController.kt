package com.example.playlistmaker.presentation

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.util.Creator

class TracksSearchController(
    private val activity: Activity,
    private val adapter: TrackAdapter
) {
    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(activity)
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private lateinit var inputEditText: EditText //by lazy { findViewById(R.id.inputEditText) }
    private lateinit var placeholderMessageText: TextView // сделать одним View с иконкой
    private lateinit var placeholderIcon: ImageView
    private lateinit var placeholderMessage: View
    private lateinit var trackListRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var clearButton: ImageView
    private lateinit var clearTrackHistoryButton: Button
    private lateinit var searchHistoryTitle: TextView
    private lateinit var refreshButton: Button
    var trackList = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    var textDump: CharSequence? = ""

    fun onCreate() {
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

        adapter.items = trackList

        trackListRecycler.layoutManager =
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
        trackListRecycler.adapter = adapter
        placeholderMessage.visibility = View.GONE

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textDump = s
                searchDebounce(textDump.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("????", "all callbacks removed")
                    handler.removeCallbacksAndMessages(null)
                }
            }

        })
    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable(""))
    }

    private fun searchRunnable(text: String) = Runnable { loadTracks(text) }
    private fun searchDebounce(text: String,) {
        Log.e("????", "all callbacks removed")
        handler.removeCallbacksAndMessages(null)
        Log.e("????", "callback added $text")
        handler.postDelayed(searchRunnable(text), SEARCH_DEBOUNCE_DELAY)
    }

    fun loadTracks(text: String) {
        if (text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            hideHistory()
            trackListRecycler.visibility = View.GONE

            tracksInteractor.searchTracks(text, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    Log.e("????? foundTracks", foundTracks.toString())
                    if (foundTracks != null) {
                       // runOnUiThread {
                            progressBar.visibility = View.GONE
                            hideMessage()
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            adapter.items = trackList
                            adapter.notifyDataSetChanged()
                            trackListRecycler.visibility = View.VISIBLE
                            hideHistory()
                       // }
                    }
                    if (errorMessage != null) {
                       // runOnUiThread {
                            showMessage(
                                text = R.string.network_error_text,
                                additionalMessage = errorMessage,
                                buttonVisibility = View.GONE,
                                icon = R.drawable.internet_error
                            )
                      //  }
                    } else if (foundTracks == null) {
                     //   runOnUiThread {
                            showMessage(
                                text = R.string.empty_song_list_error_text,
                                additionalMessage = "",
                                buttonVisibility = View.GONE,
                                icon = R.drawable.empty_results_error
                            )
                      //  }
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
}