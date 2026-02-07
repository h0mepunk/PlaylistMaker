package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist

interface PlaylistCreateInteractor {
    fun getCurrentPlaylist(): Playlist?

    fun saveCurrentPlaylist(playlist: Playlist?)
}