package com.example.playlistmaker.domain.models

sealed class TracksState {
    object Loading: TracksState()
    data class Content(
        val trackModel: ArrayList<Track>,
    ): TracksState()

    data class Empty(
        val errorMessage: String,
    ): TracksState()

    data class Error(
        val message: String,
    ): TracksState()
}