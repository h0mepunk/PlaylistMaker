package com.example.playlistmaker.domain.db

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun addTrackToPlaylist(track: Track): Flow<Track>

    fun removeTrackFromPlaylist(track: Track): Flow<Track>

    fun getTracks(): Flow<List<Track>>

}