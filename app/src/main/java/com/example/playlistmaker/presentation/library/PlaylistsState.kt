package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistsState {
    object PlaylistsEmpty : PlaylistsState()
    class PlaylistsContent(
        val playlistList: List<Playlist>
    ) : PlaylistsState()
}