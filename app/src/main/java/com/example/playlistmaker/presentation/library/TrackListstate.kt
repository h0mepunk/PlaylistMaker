package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.models.Track

sealed class TrackListState {
    object TracksEmpty : TrackListState()
    class TracksContent(
        val trackList: List<Track>
    ) : TrackListState()
}