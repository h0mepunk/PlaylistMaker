package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.presentation.track.TrackViewModel
import com.example.playlistmaker.ui.library.playlist.PlaylistCreateViewModel
import com.example.playlistmaker.ui.library.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.library.tracklist.TrackListViewModel
import com.example.playlistmaker.ui.playlist.PlaylistPageViewModel
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
        TrackViewModel(get(), get(), get(), get(), get(), )
    }

    viewModel{
        TracksSearchViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        TrackListViewModel(get())
    }

    viewModel {
        PlaylistCreateViewModel(get(), get())
    }

    viewModel {
        PlaylistPageViewModel(get(), get())
    }
}