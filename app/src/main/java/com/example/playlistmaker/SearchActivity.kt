package com.example.playlistmaker

import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    var textDump: CharSequence? = ""
    var trackList = ArrayList<Track>()

    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}

    val clearButton: ImageView by lazy { findViewById(R.id.clearIcon)}
    lateinit var adapter: TrackAdapter

    val placeholderMessage: View by lazy { findViewById(R.id.placeholderView) }
    val placeholderMessageText: TextView by lazy { findViewById(R.id.placeholderMessageText) }
    val placeholderIcon: ImageView by lazy { findViewById(R.id.placeholderIcon) }
    val refreshButton: Button by lazy { findViewById(R.id.refreshButton) }
    val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val tracksApiService = retrofit.create(TrackApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val mainActivity = Intent(this, MainActivity::class.java)
        val songListRecycler: RecyclerView by lazy { findViewById(R.id.song_list_recycler) }
        songListRecycler.layoutManager = LinearLayoutManager(this)


        adapter = TrackAdapter(trackList)
        songListRecycler.adapter = adapter
        placeholderMessage.visibility = View.GONE

        clearButton.isVisible = false
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        clearButton.setOnClickListener {
            inputEditText.setText(EMPTY_SEARCH_TEXT)
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        toolbar.setNavigationOnClickListener {
            startActivity(mainActivity)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    searchTracks(inputEditText.text)
                }
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textDump = s
            }

            override fun afterTextChanged(s: Editable?) {
                searchTracks(inputEditText.text)
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

        if (text != null) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderIcon.setBackgroundResource(icon)
            refreshButton.visibility = buttonVisibility
            trackList.clear()
            adapter.notifyDataSetChanged()

            placeholderMessageText.text = getString(text)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun searchTracks(text: CharSequence) {
        tracksApiService.getTracks(textDump.toString()).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    trackList.addAll(response.body()?.results as ArrayList<Track>)
                    Log.e("?????? not empty", "trackList: $trackList")
                    if (trackList.isEmpty()) {
                        Log.e("?????? empty", "trackList: $trackList")
                        placeholderMessage.visibility = View.VISIBLE
                        showMessage(
                            text = R.string.empty_song_list_error_text,
                            additionalMessage = "",
                            buttonVisibility = View.GONE,
                            icon = R.drawable.empty_results_error
                        )
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                } else {
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
                t.printStackTrace()
            }
        })

    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
    }
}