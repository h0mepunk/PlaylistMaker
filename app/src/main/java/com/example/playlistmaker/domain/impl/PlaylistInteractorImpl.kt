package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {

        return playlistRepository.getPlaylists()
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        return playlistRepository.insertPlaylist(playlist)
    }
}