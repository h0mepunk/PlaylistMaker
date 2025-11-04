package com.example.playlistmaker.ui.track.model

import com.example.playlistmaker.domain.models.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val movies: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data object Empty: TracksState

    object UnknownErrorState : TracksState
}