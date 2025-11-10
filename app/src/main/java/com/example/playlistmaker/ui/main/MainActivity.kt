package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.main.MainViewModel

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

        buttonMedia.setOnClickListener { viewModel.mediaButtonTap(this) }

        buttonSearch.setOnClickListener { viewModel.searchButtonTap(this) }

        buttonSettings.setOnClickListener { viewModel.settingsButtonTap(this) }
    }
}