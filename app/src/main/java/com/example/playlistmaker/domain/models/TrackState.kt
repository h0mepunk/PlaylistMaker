package com.example.playlistmaker.domain.models

sealed class TrackState {
    object Loading: TrackState()
    data class Content(
        val trackModel: Track,
    ): TrackState()
}