package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.main.MainState
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.TrackActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonMedia : Button
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSearch = findViewById(R.id.search_button)
        buttonSettings = findViewById(R.id.settings_button)
        buttonMedia = findViewById(R.id.media_button)

        viewModel.observeState().observe(this) {
            render(it)
        }

        buttonMedia.setOnClickListener { viewModel.mediaButtonTap() }

        buttonSearch.setOnClickListener { viewModel.searchButtonTap() }

        buttonSettings.setOnClickListener { viewModel.settingsButtonTap() }
    }

    fun mediaButtonTap() {
        val trackActivity = Intent(this, TrackActivity::class.java)
        startActivity(trackActivity)
    }

    fun searchButtonTap() {
        val searchActivity = Intent(this, SearchActivity::class.java)
        startActivity(searchActivity)
    }

    fun settingsButtonTap() {
        val settingsActivity = Intent(this, SettingsActivity::class.java)
        startActivity(settingsActivity)
    }

    fun render(mainState: MainState) {
        when (mainState) {
            is MainState.Settings -> {
                settingsButtonTap()
            }
            is MainState.Search -> {
                searchButtonTap()
            }
            is MainState.Track ->  {
                mediaButtonTap()
            }
        }
    }
}