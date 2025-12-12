package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.presentation.main.MainState
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.ui.search.SearchFragment
import com.example.playlistmaker.ui.settings.SettingsFragment
import com.example.playlistmaker.ui.track.TrackFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonMedia : Button

    private lateinit var binding: FragmentMainBinding

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = FragmentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
       supportFragmentManager.commit {
           replace(R.id.fragment_main, TrackFragment())
       }
    }

    fun searchButtonTap() {
        supportFragmentManager.commit {
            replace(R.id.fragment_main, SearchFragment())
        }
    }

    fun settingsButtonTap() {
        supportFragmentManager.commit {
            replace(R.id.fragment_main, SettingsFragment())
        }
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