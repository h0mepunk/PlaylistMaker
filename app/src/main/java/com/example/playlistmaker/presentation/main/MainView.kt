package com.example.playlistmaker.presentation.main

interface MainView {

    fun onMediaButtonTap(action: () -> Unit)
    fun onSearchButtonTap(action: () -> Unit)
    fun onSettingsButtonTap(action: () -> Unit)

}