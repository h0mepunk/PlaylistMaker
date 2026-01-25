package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl (
        private val libraryRepository: LibraryRepository
    ) : PlaylistInteractor {

    override suspend fun addTrackToPlaylist(track: Track)
    { return libraryRepository.addTrackToPlaylist(track) }

    override suspend fun removeTrackFromPlaylist(track: Track)
    { return libraryRepository.removeTrackFromPlaylist(track) }

    override fun getTracks(): Flow<List<Track>> {
        return libraryRepository.getTracks()
    }

    override fun getPlaylists(): Flow<List<Playlist>> {

        return libraryRepository.getPlaylists()
    }
}