package com.example.playlistmaker.ui.search

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchPresenter
import com.example.playlistmaker.presentation.search.TracksSearchView
import com.example.playlistmaker.ui.track.TrackAdapter

class SearchActivity : AppCompatActivity(), TracksSearchView {
    private lateinit var adapter: TrackAdapter
    private lateinit var tracksSearchPresenter: TracksSearchPresenter
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
        tracksSearchPresenter= Creator.provideTracksSearchPresenter(this, this)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        refreshButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            tracksSearchPresenter.searchRequest(tracksSearchPresenter.lastSearchText.toString())
        }
        clearButton.setOnClickListener {
            searchText.setText(EMPTY_SEARCH_TEXT)
            tracksSearchPresenter.showHistory()
            inputMethodManager?.hideSoftInputFromWindow(searchText.windowToken, 0)
            tracksSearchPresenter.trackList.clear()
            updateTracksList(tracksSearchPresenter.trackList)
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        clearTrackHistoryButton.setOnClickListener {
            tracksSearchPresenter.trackHistory.clear()
            tracksSearchPresenter.trackHistoryInteractor.saveTracksHistory(tracksSearchPresenter.trackHistory)
            tracksSearchPresenter.hideHistory()
        }

        searchText.setOnFocusChangeListener() { _, hasFocus -> }
        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(searchText.windowToken, 0)
                    tracksSearchPresenter.lastSearchText = searchText.text.toString()
                    tracksSearchPresenter.searchRequest(tracksSearchPresenter.lastSearchText.toString())
                }
            }
            false
        }

         textWatcher =    object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = s?.toString()?:""
                tracksSearchPresenter.lastSearchText = searchText
                tracksSearchPresenter.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("TracksSearchController", "all callbacks removed")
                    tracksSearchPresenter.handler.removeCallbacksAndMessages(null)
                    tracksSearchPresenter.showHistory()
                }
                if((searchText.hasFocus()) && s.isNullOrEmpty()) {
                    tracksSearchPresenter.showHistory()
                }
            }

        }
        textWatcher?.let { searchText.addTextChangedListener(it) }
        tracksSearchPresenter.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksSearchPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracksSearchPresenter.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tracksSearchPresenter.onRestoreInstanceState(savedInstanceState)
    }

    override fun showPlaceholderMessage(isVisible: Boolean) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showTracksList(isVisible: Boolean) {
        tracksListRecycler.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun  showHistory(isVisible: Boolean) {
        searchHistoryTitle.visibility = if (isVisible)View.VISIBLE else View.GONE
        clearTrackHistoryButton.visibility = if (isVisible)View.VISIBLE else View.GONE
    }

    override fun changePlaceholderMessage(text: String) {
        placeholderMessageText.text = text
    }

    override fun showRefreshButton(isVisible: Boolean) {
        refreshButton.visibility = if (isVisible)View.VISIBLE else View.GONE
    }

    override fun setEditText(text: String?) {
        searchText.setText(text?:"")
    }

    override fun setPlaceholderIcon(resId: Int) {
        placeholderIcon.setBackgroundResource(resId)
    }

    override fun updateTracksList(newTracksList: List<Track>) {
        Log.e("SearchActivity", "trackList = $newTracksList")
        adapter.items = newTracksList
        adapter.notifyDataSetChanged()
    }

    override fun showToast(message: String) {
        Log.e("TracksSearchController", "showToast: $message")
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        const val EMPTY_SEARCH_TEXT = ""
    }

}