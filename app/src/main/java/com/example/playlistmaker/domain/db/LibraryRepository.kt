package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)

    fun getTracks(): Flow<List<Track>>

    fun getTrackById(trackId: Int): Flow<Track>
}