package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.LibraryInteractor
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class LibraryInteractorImpl (
        private val libraryRepository: LibraryRepository
    ) : LibraryInteractor {

    override fun getTracks(): Flow<List<Track>> {
        return libraryRepository.getTracks()
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return libraryRepository.getTrackById(trackId)
    }

    override suspend fun addTrackToFavorites(track: Track) {
        return libraryRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        return libraryRepository.deleteTrackFromFavorites(track)
    }
}