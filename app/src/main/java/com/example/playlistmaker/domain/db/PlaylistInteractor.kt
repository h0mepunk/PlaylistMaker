package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    suspend fun addTrackToPlaylist(playlistId: Int, trackId: String)

    suspend fun insertTrack(track: Track)

    fun getTrackById(id: Int): Flow<Track>
}