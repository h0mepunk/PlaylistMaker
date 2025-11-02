package com.example.playlistmaker.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.track.TrackAdapter

class SearchActivity : AppCompatActivity() {
    private lateinit var adapter: TrackAdapter
    private val tracksSearchController = Creator.provideTracksSearchController(this, adapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        tracksSearchController.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksSearchController.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracksSearchController.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tracksSearchController.onRestoreInstanceState(savedInstanceState)
    }
}