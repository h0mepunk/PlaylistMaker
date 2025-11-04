package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.ui.track.model.TracksState

interface TracksSearchView {
    fun render(state: TracksState)

    fun showToast(additionalMessage: String)
}