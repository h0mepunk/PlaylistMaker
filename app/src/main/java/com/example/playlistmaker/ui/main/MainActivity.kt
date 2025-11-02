package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.main.MainView

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var mediaButton : Button
    private val mainPresenter = Creator.provideMainPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonSearch = findViewById(R.id.search_button)
        buttonSettings = findViewById(R.id.settings_button)
        mediaButton = findViewById(R.id.media_button)
        mainPresenter.onCreate()
    }

    override fun onMediaButtonTap(action: () -> Unit) {
        mediaButton.setOnClickListener { action() }
    }

    override fun onSearchButtonTap(
        action: () -> Unit
    ) {
        buttonSearch.setOnClickListener { action() }
    }

    override fun onSettingsButtonTap(action: () -> Unit) {
        buttonSettings.setOnClickListener { action() }
    }
}