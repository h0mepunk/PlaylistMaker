package com.example.playlistmaker.presentation.track

import com.example.playlistmaker.R

sealed class TrackState(
    val isPlayButtonEnabled: Boolean,
    val buttonImage: Int,
    val timerText: String,
) {

    class Init(
        val previewImgUrl: String,
    ) : TrackState(
        false,
        R.drawable.media_play,
        "00:00"
    )

    class Playing(
        val trackTime: String?
    ) : TrackState(
        true,
        R.drawable.media_stop,
        trackTime?:"00:00",
    )

    class Paused(
        val trackTime: String?,
    ): TrackState(
        true,
        R.drawable.media_play,
        trackTime?:"00:00",
    )

    object Stopped: TrackState(
        false,
        R.drawable.media_play,
        "00:00"

    )

    object Prepared: TrackState(
        true,
        R.drawable.media_play,
        "00:00"
    )
}