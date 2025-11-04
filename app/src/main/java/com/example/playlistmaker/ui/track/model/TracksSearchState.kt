package com.example.playlistmaker.ui.track.model

import com.example.playlistmaker.domain.models.Track

sealed interface TracksSearchState {

    object Loading : TracksSearchState

    data class Content(
        val movies: List<Track>
    ) : TracksSearchState

    data class Error(
        val errorMessage: String
    ) : TracksSearchState

    data object Empty: TracksSearchState

    object UnknownErrorSearchState : TracksSearchState
}