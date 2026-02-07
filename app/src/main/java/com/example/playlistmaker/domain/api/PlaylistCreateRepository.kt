package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist

interface PlaylistCreateRepository{
    fun getCurrentPlaylist(): Playlist?

    fun saveCurrentPlaylist(playlist: Playlist?)
}