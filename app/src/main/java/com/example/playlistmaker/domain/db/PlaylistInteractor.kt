package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun addTrackToPlaylist(track: Track): Flow<Track>

    fun removeTrackFromPlaylist(track: Track): Flow<Track>

    fun getTracks(): Flow<List<Track>>
}