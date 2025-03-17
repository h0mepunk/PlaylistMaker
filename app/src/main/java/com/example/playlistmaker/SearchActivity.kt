package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    var textDump: CharSequence? = ""
    var trackList = ArrayList<Track>()
    var trackHistory = ArrayList<Track>()

    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}

    val clearButton: ImageView by lazy { findViewById(R.id.clearIcon)}
    lateinit var adapter: TrackAdapter
    lateinit var historyAdapter: TrackHistoryAdapter
    val sharedPreferences by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)}

    val placeholderMessage: View by lazy { findViewById(R.id.placeholderView) }
    val placeholderMessageText: TextView by lazy { findViewById(R.id.placeholderMessageText) }
    val placeholderIcon: ImageView by lazy { findViewById(R.id.placeholderIcon) }
    val songListRecycler: RecyclerView by lazy { findViewById(R.id.song_list_recycler) }
    val refreshButton: Button by lazy { findViewById(R.id.refreshButton) }
    val clearTrackHistoryButton: Button by lazy { findViewById(R.id.clearHistoryButton) }
    val trackHistoryRecycler: RecyclerView by lazy { findViewById(R.id.song_history_list_recycler) }
    val searchHistoryLayout: View by lazy { findViewById(R.id.searchHistoryLayout) }
    val progressBar: ProgressBar by lazy { findViewById(R.id.searchProgressBar) }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val tracksApiService = retrofit.create(TrackApiService::class.java)
    val trackDataProcessor = TrackDataProcessor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        songListRecycler.layoutManager = LinearLayoutManager(this)
        trackHistoryRecycler.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(trackList, this)

        trackHistory = getTrachHistory()
        historyAdapter = TrackHistoryAdapter(trackHistory, this)
        trackHistoryRecycler.adapter = historyAdapter
        songListRecycler.adapter = adapter
        //trackHistoryRecycler.adapter = historyAdapter
        placeholderMessage.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE

        clearButton.isVisible = false
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        if (trackList.isEmpty()) {
            showHistory()
        }

        refreshButton.setOnClickListener {
            searchTracks(textDump.toString())
        }

        clearButton.setOnClickListener {
            inputEditText.setText(EMPTY_SEARCH_TEXT)
            showHistory()
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackList.clear()
            adapter.notifyDataSetChanged()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearTrackHistoryButton.setOnClickListener {
            trackHistory.clear()
            sharedPreferences.edit().putString(TRACK_HISTORY_LIST_KEY, "").apply()
            historyAdapter.notifyDataSetChanged()
            searchHistoryLayout.visibility = View.GONE
        }

        inputEditText.setOnFocusChangeListener() { _, hasFocus -> }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    textDump = inputEditText.text
                    searchTracks(textDump.toString())
                }
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {

            private fun searchRunnable(text: String) = Runnable { searchTracks(text) }

            private fun searchDebounce(text: String) {
                handler.removeCallbacks(searchRunnable(text))
                handler.postDelayed(searchRunnable(text), SEARCH_DEBOUNCE_DELAY)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textDump = s
                if(!(inputEditText.hasFocus()) && s.isNullOrEmpty()) {
                    showHistory()
                }
                searchDebounce(textDump.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
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
        adapter.notifyDataSetChanged()
        searchHistoryLayout.visibility = View.GONE
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

    fun getTrachHistory(): ArrayList<Track> {
        return trackDataProcessor.tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")
        )
    }

    fun showHistory() {
        trackHistory = getTrachHistory()
        historyAdapter.notifyDataSetChanged()
        if (trackHistory.isNotEmpty()) {
            searchHistoryLayout.visibility = View.VISIBLE
        } else {
            searchHistoryLayout.visibility = View.GONE
        }
    }

    private fun searchTracks(text: String) {
        if (text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            searchHistoryLayout.visibility = View.GONE
            songListRecycler.visibility = View.GONE

            tracksApiService.getTracks(text).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.code() == 200) {
                        progressBar.visibility = View.GONE
                        placeholderMessage.visibility = View.GONE
                        trackList.clear()
                        trackList.addAll(response.body()?.results as ArrayList<Track>)
                        if (trackList.isEmpty()) {
                            showMessage(
                                text = R.string.empty_song_list_error_text,
                                additionalMessage = "",
                                buttonVisibility = View.GONE,
                                icon = R.drawable.empty_results_error
                            )
                        } else {
                            adapter.notifyDataSetChanged()
                            songListRecycler.visibility = View.VISIBLE
                            searchHistoryLayout.visibility = View.GONE
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        val errorJson = response.errorBody()?.string()
                        showMessage(
                            text = R.string.network_error_text,
                            additionalMessage = errorJson.toString(),
                            buttonVisibility = View.VISIBLE,
                            icon = R.drawable.internet_error
                        )
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    t.printStackTrace()
                    showMessage(
                        text = R.string.network_error_text,
                        additionalMessage = "",
                        buttonVisibility = View.VISIBLE,
                        icon = R.drawable.internet_error
                    )
                }
            })
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}