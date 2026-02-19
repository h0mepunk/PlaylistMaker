package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.models.Playlist

class PlaylistCreateInteractorImpl(
    private val repository: PlaylistCreateRepository
) : PlaylistCreateInteractor {

    override fun getCurrentPlaylist(): Playlist? {
        return repository.getCurrentPlaylist()
    }

    override fun saveCurrentPlaylist(playlist: Playlist?) {
        repository.saveCurrentPlaylist(playlist)
    }
}