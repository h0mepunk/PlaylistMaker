package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.domain.api.TracksInteractor

class SearchActivity : AppCompatActivity() {
    var textDump: CharSequence? = ""
    var trackHistory = ArrayList<Track>()
    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}
    val clearButton: ImageView by lazy { findViewById(R.id.clearIcon)}
    private lateinit var adapter: TrackAdapter
    private lateinit var sharedPreferences : SharedPreferences
    val songListRecycler: RecyclerView by lazy { findViewById(R.id.song_list_recycler) }
    val refreshButton: Button by lazy { findViewById(R.id.refreshButton) }
    val clearTrackHistoryButton: Button by lazy { findViewById(R.id.clearHistoryButton) }
    val historyTitle: TextView by lazy { findViewById(R.id.searchHistoryTitle) }
    val progressBar: ProgressBar by lazy { findViewById(R.id.searchProgressBar) }
    private lateinit var trackHistoryInteractor : TracksHistoryInteractor
    private var tracksInteractor: TracksInteractor = Creator.provideTracksInteractor(this)
    private val tracksSearchController = Creator.provideTracksSearchController(this, adapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tracksSearchController.onCreate()
        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor(this)
        sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()
        adapter = TrackAdapter()
        showHistory()


        clearButton.isVisible = false
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            tracksSearchController.loadTracks(textDump.toString())
        }

        clearButton.setOnClickListener {
            inputEditText.setText(EMPTY_SEARCH_TEXT)
            showHistory()
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            tracksSearchController.trackList.clear()
            adapter.items = tracksSearchController.trackList
            adapter.notifyDataSetChanged()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearTrackHistoryButton.setOnClickListener {
            trackHistory.clear()
            trackHistoryInteractor.saveTracksHistory(trackHistory)
            tracksSearchController.hideHistory()
        }

        inputEditText.setOnFocusChangeListener() { _, hasFocus -> }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    textDump = inputEditText.text
                    tracksSearchController.loadTracks(textDump.toString())
                }
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    showHistory()
                }
                if((inputEditText.hasFocus()) && s.isNullOrEmpty()) {
                    showHistory()
                }
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            textDump = savedInstanceState.getCharSequence(
                SEARCH_TEXT,
                EMPTY_SEARCH_TEXT as CharSequence
            )
        }
    }

    fun showHistory() {
        trackHistory = trackHistoryInteractor.getTracksHistory()
        Log.e("????", trackHistory.toString())
        if (trackHistory.isNotEmpty()) {
            historyTitle.visibility = View.VISIBLE
            clearTrackHistoryButton.visibility = View.VISIBLE
            tracksSearchController.hideMessage()
            adapter.items = trackHistory
            adapter.notifyDataSetChanged()
            songListRecycler.visibility = View.VISIBLE
        } else {
            tracksSearchController.hideHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksSearchController.onDestroy()
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
    }
}