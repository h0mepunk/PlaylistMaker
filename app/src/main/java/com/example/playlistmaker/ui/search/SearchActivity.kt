package com.example.playlistmaker.ui.search

import TracksSearchViewModel
import android.content.Context
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
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.TracksState
import com.example.playlistmaker.ui.track.TrackAdapter.Companion.CLICK_DEBOUNCE_DELAY

class SearchActivity : ComponentActivity() {

    var textDump: CharSequence? = ""
    var trackList = ArrayList<Track>()
    var trackHistory = ArrayList<Track>()
    private var isClickAllowed = true

    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}
    val clearButton: ImageView by lazy { findViewById(R.id.clearIcon)}
    private lateinit var adapter: TrackAdapter
    private lateinit var sharedPreferences : SharedPreferences
    val placeholderMessage: View by lazy { findViewById(R.id.placeholderView) }
    val placeholderMessageText: TextView by lazy { findViewById(R.id.placeholderMessageText) }
    val placeholderIcon: ImageView by lazy { findViewById(R.id.placeholderIcon) }
    val songListRecycler: RecyclerView by lazy { findViewById(R.id.song_list_recycler) }
    val refreshButton: Button by lazy { findViewById(R.id.refreshButton) }
    val clearTrackHistoryButton: Button by lazy { findViewById(R.id.clearHistoryButton) }
    val historyTitle: TextView by lazy { findViewById(R.id.searchHistoryTitle) }
    val progressBar: ProgressBar by lazy { findViewById(R.id.searchProgressBar) }
    private lateinit var trackHistoryInteractor : TracksHistoryInteractor
    private lateinit var tracksInteractor: TracksInteractor
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewModel: TracksSearchViewModel
    private lateinit var textWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, TracksSearchViewModel.getViewModelFactory())[TracksSearchViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor(applicationContext)
        sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()
        songListRecycler.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter()

//        showHistory()
//        songListRecycler.adapter = adapter
//        placeholderMessage.visibility = View.GONE

//        clearButton.isVisible = false
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        refreshButton.setOnClickListener {
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
            finish()
        }

        clearTrackHistoryButton.setOnClickListener {
            trackHistory.clear()
            trackHistoryInteractor.saveTracksHistory(trackHistory)
            historyTitle.visibility = View.GONE
            clearTrackHistoryButton.visibility = View.GONE
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

        val textWatcher = object : TextWatcher {

            private fun searchRunnable(text: String) = Runnable { loadTracks(text) }

            private fun searchDebounce(text: String,) {
                Log.e("????", "all callbacks removed")
                handler.removeCallbacksAndMessages(null)
                Log.e("????", "callback added $text")
                handler.postDelayed(searchRunnable(text), SEARCH_DEBOUNCE_DELAY)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textDump = s
                searchDebounce(textDump.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("????", "all callbacks removed")
                    handler.removeCallbacksAndMessages(null)
                    showHistory()
                }
                if((inputEditText.hasFocus()) && s.isNullOrEmpty()) {
                    showHistory()
                }
            }
        }
        textWatcher?.let { inputEditText.addTextChangedListener(it) }
        //inputEditText.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            textDump = savedInstanceState.getCharSequence(
                SEARCH_TEXT,
                EMPTY_SEARCH_TEXT as CharSequence
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, textDump)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textDump = savedInstanceState.getCharSequence(
            SEARCH_TEXT,
            EMPTY_SEARCH_TEXT as CharSequence
        )
        inputEditText.setText(textDump)
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
        historyTitle.visibility = View.GONE
        clearTrackHistoryButton.visibility = View.GONE
        if (text != null) {
            placeholderMessageText.text = getString(text)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }

            placeholderMessage.visibility = View.VISIBLE
            placeholderIcon.setBackgroundResource(icon)
            refreshButton.visibility = buttonVisibility

        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    fun showHistory() {
        trackHistory = trackHistoryInteractor.getTracksHistory()
        Log.e("????", trackHistory.toString())
        if (trackHistory.isNotEmpty()) {
            historyTitle.visibility = View.VISIBLE
            clearTrackHistoryButton.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            adapter.items = trackHistory
            adapter.notifyDataSetChanged()
            songListRecycler.visibility = View.VISIBLE
        } else {
            historyTitle.visibility = View.GONE
            clearTrackHistoryButton.visibility = View.GONE
        }
    }

    private fun loadTracks(text: String) {
        if (text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            historyTitle.visibility = View.GONE
            clearTrackHistoryButton.visibility = View.GONE
            songListRecycler.visibility = View.GONE

            tracksInteractor.searchTracks(text, object: TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                        Log.e("????? foundTracks", foundTracks.toString())
                        if (foundTracks.isNotEmpty()) {
                            runOnUiThread {
                            progressBar.visibility = View.GONE
                            placeholderMessage.visibility = View.GONE
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            adapter.items = trackList
                            adapter.notifyDataSetChanged()
                            songListRecycler.visibility = View.VISIBLE
                                historyTitle.visibility = View.GONE
                                clearTrackHistoryButton.visibility = View.GONE
                            }
                        } else {
                            runOnUiThread {
                                showMessage(
                                    text = R.string.empty_song_list_error_text,
                                    additionalMessage = "",
                                    buttonVisibility = View.GONE,
                                    icon = R.drawable.empty_results_error
                                )
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

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.trackModel)
            is TracksState.Empty -> showEmpty(state.errorMessage)
            is TracksState.Error -> showError(state.message)
            is TracksState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
//        songListRecycler.visibility = View.GONE
//        placeholderMessage.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
    }

    private fun showError(errorMessage: String) {
//        songListRecycler.visibility = View.GONE
//        placeholderMessage.visibility = View.VISIBLE
//        progressBar.visibility = View.GONE
//
//        placeholderMessageText.text = errorMessage
    }

    private fun showEmpty(emptyMessage: String) {
        //showError(emptyMessage)
    }

    private fun showContent(tracks: List<Track>) {
//        songListRecycler.visibility = View.VISIBLE
//        placeholderMessage.visibility = View.GONE
//        progressBar.visibility = View.GONE
//
//        trackList.clear()
//        trackList.addAll(tracks)
//        adapter.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}