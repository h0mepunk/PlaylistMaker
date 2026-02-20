package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
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

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: String) {
        return playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>> {
        return  playlistRepository.getTracksFromPlaylist(playlistId)
    }

    override suspend fun insertTrack(track: Track) {
        return playlistRepository.insertTrack(track)
    }

    override fun getTrackById(id: Int): Flow<Track> {
        return playlistRepository.getTrackById(id)
    }
}