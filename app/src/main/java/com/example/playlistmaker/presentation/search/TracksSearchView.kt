package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track
interface TracksSearchView {
    fun showUnknownError()

    fun showToast(message: String)

    fun showLoading()

    fun showContent(tracks: List<Track>)

    fun showError(message: String?)

    fun showEmpty(message: String?)

    fun showHistory(tracks: List<Track>)
}