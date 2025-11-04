package com.example.playlistmaker.ui.track.model

import com.example.playlistmaker.domain.models.Track

interface TrackState {

    class Playing(
        val trackTime: String?,
    ) : TrackState

    object Paused: TrackState

    object Stopped: TrackState
}