package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val mainActivity = Intent(this, MainActivity::class.java)
        val songListRecycler: RecyclerView by lazy { findViewById(R.id.song_list_recycler) }
        songListRecycler.layoutManager = LinearLayoutManager(this)

        val tracksApiService = retrofit.create(TrackApiService::class.java)

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
                    tracksApiService.getTracks(textDump.toString()).enqueue(object : Callback<TrackResponse> {
                        override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                            if (response.isSuccessful) {
                                trackList = response.body()?.results as ArrayList<Track>
                            } else {
                                val errorJson = response.errorBody()?.string()
                                showMessage(text = R.string.network_error_text, additionalMessage = errorJson.toString())
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                    if (trackList.isEmpty()) {
                        placeholderMessage.visibility = View.VISIBLE
                        showMessage(text = R.string.network_error_text, additionalMessage = "")
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
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
                // empty
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

    private fun showMessage(text: Int?, additionalMessage: String) {
        if (text != null) {
            placeholderMessage.visibility = View.VISIBLE
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
        val TRACKS: ArrayList<Track> = arrayListOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }
}