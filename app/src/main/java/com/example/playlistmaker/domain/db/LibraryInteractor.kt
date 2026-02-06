package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryInteractor {
    suspend fun addTrackToPlaylist(track: Track)

    suspend fun removeTrackFromPlaylist(track: Track)

    fun getTracks(): Flow<List<Track>>
}