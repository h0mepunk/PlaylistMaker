package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track
interface TracksSearchView {
    fun showPlaceholderMessage(isVisible: Boolean)

    fun showTracksList(isVisible: Boolean)

    fun showProgressBar(isVisible: Boolean)

    fun showHistory(isVisible: Boolean)

    fun showRefreshButton(isVisible: Boolean)

    fun changePlaceholderMessage(text: String)

    fun setEditText(text: String?)

    fun setPlaceholderIcon(resId: Int)

    fun updateTracksList(newTracksList: List<Track>)
}