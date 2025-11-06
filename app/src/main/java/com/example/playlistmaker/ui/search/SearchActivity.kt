package com.example.playlistmaker.ui.search

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
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchView
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.ui.track.model.TracksSearchState
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class SearchActivity : MvpAppCompatActivity(), TracksSearchView {
    private lateinit var adapter: TrackAdapter
    private val presenter by moxyPresenter {
        Creator.provideTracksSearchPresenter(applicationContext)
    }
    private lateinit var searchText: EditText
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderIcon: ImageView
    private lateinit var placeholderMessage: View
    private lateinit var tracksListRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var clearButton: ImageView
    private lateinit var clearTrackHistoryButton: Button
    private lateinit var searchHistoryTitle: TextView
    private lateinit var refreshButton: Button

    private lateinit var toolbar: Toolbar
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholderView)
        placeholderMessageText = findViewById(R.id.placeholderMessageText)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        searchText = findViewById(R.id.searchText)
        tracksListRecycler = findViewById(R.id.song_list_recycler)
        progressBar = findViewById(R.id.searchProgressBar)
        clearButton =  findViewById(R.id.clearIcon)
        clearTrackHistoryButton = findViewById(R.id.clearHistoryButton)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        refreshButton = findViewById(R.id.refreshButton)
        toolbar = findViewById(R.id.search_toolbar)

        adapter = TrackAdapter()
        tracksListRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksListRecycler.adapter = adapter

        placeholderMessage.visibility = View.GONE
        clearButton.isVisible = false

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        updateTracksList(presenter.trackList)

        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            presenter.searchRequest(presenter.lastSearchText.toString())
        }

        clearButton.setOnClickListener {
            searchText.setText(EMPTY_SEARCH_TEXT)
            showHistory(presenter.getHistory())
            inputMethodManager?.hideSoftInputFromWindow(searchText.windowToken, 0)
            presenter.trackList.clear()
            updateTracksList(presenter.trackList)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearTrackHistoryButton.setOnClickListener {
            presenter.trackHistoryInteractor.saveTracksHistory(ArrayList())
            adapter.items = emptyList()
            adapter.notifyDataSetChanged()
            searchHistoryTitle.visibility = View.GONE
            clearTrackHistoryButton.visibility = View.GONE
        }

        searchText.setOnFocusChangeListener() { _, hasFocus -> }
        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(searchText.windowToken, 0)
                    presenter.lastSearchText = searchText.text.toString()
                    presenter.searchRequest(presenter!!.lastSearchText.toString())
                }
            }
            false
        }

         textWatcher =    object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = s?.toString()?:""
                presenter.lastSearchText = searchText
                presenter.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("TracksSearchController", "all callbacks removed")
                    presenter.handler.removeCallbacksAndMessages(null)
                    showHistory(presenter.getHistory())
                }
                if((searchText.hasFocus()) && s.isNullOrEmpty()) {
                    showHistory(presenter.getHistory())
                }
            }

        }
        textWatcher?.let { searchText.addTextChangedListener(it) }
        presenter.onCreate()
        showHistory(presenter.getHistory())
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { searchText.removeTextChangedListener(it) }
        presenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText.setText(presenter.onRestoreInstanceState(savedInstanceState)?:"")
    }

    fun updateTracksList(newTracksList: List<Track>) {
        Log.e("SearchActivity", "trackList = $newTracksList")
        adapter.items = newTracksList
        adapter.notifyDataSetChanged()
    }

    fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE

        Log.e("SearchActivity", "trackList = $tracks")
        adapter.items = tracks
        adapter.notifyDataSetChanged()

        tracksListRecycler.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.GONE
        clearTrackHistoryButton.visibility = View.GONE
    }

    fun showError(messageId: Int, iconId: Int) {
        Log.e("SearchActivity", "trackList loading error")

        presenter.trackList.clear()
        adapter.items = emptyList()
        adapter.notifyDataSetChanged()

        searchHistoryTitle.visibility = View.GONE
        clearTrackHistoryButton.visibility = View.GONE

        placeholderMessageText.text = getString(messageId)
        placeholderIcon.setBackgroundResource(iconId)
        placeholderMessage.visibility = View.VISIBLE

        refreshButton.visibility = View.VISIBLE
    }

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        tracksListRecycler.visibility = View.GONE
        searchHistoryTitle.visibility = View.GONE
        clearTrackHistoryButton.visibility = View.GONE
    }

    fun showHistory(tracks: List<Track>) {
        Log.e("TracksSearchController", tracks.toString())
        if (tracks.isNotEmpty()){
            searchHistoryTitle.visibility = View.VISIBLE
            clearTrackHistoryButton.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            Log.e("SearchActivity", "trackhistory = $tracks")
            adapter.items = tracks
            adapter.notifyDataSetChanged()
            tracksListRecycler.visibility = View.VISIBLE
        } else {
            searchHistoryTitle.visibility = View.GONE
            clearTrackHistoryButton.visibility = View.GONE
        }
    }

    fun showUnknownError() {
        placeholderMessage.visibility = View.GONE
    }

    override fun render(state: TracksSearchState) {
        when (state) {
            is TracksSearchState.Loading -> showLoading()
            is TracksSearchState.Error -> showError(
                messageId = R.string.network_error_text,
                iconId = R.drawable.internet_error
            )

            is TracksSearchState.Content -> showContent(state.movies)
            is TracksSearchState.Empty -> showError(
                messageId = R.string.empty_song_list_error_text,
                iconId = R.drawable.empty_results_error
            )

            is TracksSearchState.UnknownErrorSearchState -> showUnknownError()
        }
    }

    override fun showToast(additionalMessage: String) {
        Log.e("SearchActivity", "showToast: $additionalMessage")
        runOnUiThread {
            Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        const val EMPTY_SEARCH_TEXT = ""
    }

}