package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.TracksSearchViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonMedia : Button
    private var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.getFactory()
        )[MainViewModel::class.java]

        buttonSearch = findViewById(R.id.search_button)
        buttonSettings = findViewById(R.id.settings_button)
        buttonMedia = findViewById(R.id.media_button)

        buttonMedia.setOnClickListener { viewModel?.mediaButtonTap() }

        buttonSearch.setOnClickListener { viewModel?.searchButtonTap() }

        buttonSettings.setOnClickListener { viewModel?.settingsButtonTap() }
    }
}