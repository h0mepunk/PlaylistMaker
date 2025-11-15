package com.example.playlistmaker.presentation.track

interface TrackState {

    class Init(
        val previewImgUrl: String,
    ) : TrackState

    class Playing(
        val trackTime: String?,
    ) : TrackState

    object Paused: TrackState

    object Stopped: TrackState

    object Prepared: TrackState
}