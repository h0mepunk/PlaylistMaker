package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistCreateState {

    object PlaylistEmpty : PlaylistCreateState()

    class PlaylistContent(
        val playlist: Playlist
    ) : PlaylistCreateState()
}