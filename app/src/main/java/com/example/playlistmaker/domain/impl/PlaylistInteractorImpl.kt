package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl (
        private val playlistRepository: PlaylistRepository
    ) : PlaylistInteractor {

    override fun addTrackToPlaylist(track: Track): Flow<Track> {
            return playlistRepository.addTrackToPlaylist(track)
        }

    override fun removeTrackFromPlaylist(track: Track): Flow<Track> {
        return playlistRepository.removeTrackFromPlaylist(track)
    }

    override fun getTracks(): Flow<List<Track>> {
        return playlistRepository.getTracks()
    }
}