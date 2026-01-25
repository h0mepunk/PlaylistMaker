package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl (
        private val playlistRepository: PlaylistRepository
    ) : PlaylistInteractor {

    override suspend fun addTrackToPlaylist(track: Track)
    { return playlistRepository.addTrackToPlaylist(track) }

    override suspend fun removeTrackFromPlaylist(track: Track)
    { return playlistRepository.removeTrackFromPlaylist(track) }

    override fun getTracks(): Flow<List<Track>> {
        return playlistRepository.getTracks()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {

        return playlistRepository.getPlaylists()
    }
}