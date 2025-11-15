package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.presentation.track.TrackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel()
    }

    viewModel {
        SettingsViewModel()
    }

    viewModel {
        TrackViewModel(get(), get(), get())
    }

    viewModel{
        TracksSearchViewModel(get(), get())
    }
}