package com.example.playlistmaker.presentation.playlist

import com.example.playlistmaker.domain.models.Track

sealed class PlaylistPageState {

    data object Empty : PlaylistPageState()

    data class Tracks(
        val tracks: List<Track>
    ) : PlaylistPageState()
}