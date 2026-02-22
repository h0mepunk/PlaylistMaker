package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    suspend fun addTrackToPlaylist(playlistId: Int, trackId: String, trackTime: Long)

    fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    fun getTrackById(id: Int): Flow<Track>
}