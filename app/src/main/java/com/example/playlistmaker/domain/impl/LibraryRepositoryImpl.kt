package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor,
) : LibraryRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = trackDao.getTracks()
        emit(convertFromTracksEntity(tracks))
    }

    override fun getTrackById(trackId: Int): Flow<Track> = flow {
        val trackEntity = trackDao.getTrackById(trackId)
        trackEntity.let { trackDbConvertor.map(it)}
    }

    override suspend fun addTrackToFavorites(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        trackDao.deleteTrack(trackDbConvertor.map(track))
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
