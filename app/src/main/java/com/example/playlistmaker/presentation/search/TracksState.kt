package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class History(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data object Empty: TracksState

    data object Initial: TracksState

    object UnknownErrorState : TracksState
}