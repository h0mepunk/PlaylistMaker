package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.models.Playlist
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

    override suspend fun addTrackToPlaylist(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromPlaylist(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDao.deleteTrack(trackEntity)
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
