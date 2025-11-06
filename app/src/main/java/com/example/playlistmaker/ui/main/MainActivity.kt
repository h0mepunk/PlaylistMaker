package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.main.MainView
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.MediaActivity
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonMedia : Button

    private val presenter by moxyPresenter {
        Creator.provideMainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonSearch = findViewById(R.id.search_button)
        buttonSettings = findViewById(R.id.settings_button)
        buttonMedia = findViewById(R.id.media_button)

        buttonSearch.setOnClickListener { presenter.onSearchClicked() }
        buttonSettings.setOnClickListener { presenter.onSettingsClicked() }
        buttonMedia.setOnClickListener { presenter.onMediaClicked() }
    }

    override fun openSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }

    override fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun openMedia() {
        startActivity(Intent(this, MediaActivity::class.java))
    }
}