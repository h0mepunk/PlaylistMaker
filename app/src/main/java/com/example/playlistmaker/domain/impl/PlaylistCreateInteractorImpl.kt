package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.models.Playlist

class PlaylistCreateInteractorImpl(
    private val repository: PlaylistCreateRepository
) : PlaylistCreateInteractor {

    override fun getCurrentCreatingPlaylist(): Playlist? {
        return repository.getCurrentCreatingPlaylist()
    }

    override fun saveCurrentCreatingPlaylist(playlist: Playlist?) {
        repository.saveCurrentCreatingPlaylist(playlist)
    }

    override fun getCurrentPlaylist(): Playlist? {
        return repository.getCurrentPlaylist()
    }

    override fun saveCurrentPlaylist(playlist: Playlist?) {
        return repository.saveCurrentPlaylist(playlist)
    }

    override fun getIsEditedFlag(): Boolean {
        return repository.getIsEditedFlag()
    }

    override fun setIsEditedFlag(isEdited: Boolean) {
        return repository.setIsEditedFlag(isEdited)
    }
}