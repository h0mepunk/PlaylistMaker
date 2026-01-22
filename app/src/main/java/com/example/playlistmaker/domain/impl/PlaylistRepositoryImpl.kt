package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTracksEntity(tracks))
    }

    override fun addTrackToPlaylist(track: Track): Flow<Track> = flow {
        val track = appDatabase.trackDao().insertTrack(track)
        emit(convertFromTrackEntity(track))
    }

    override fun removeTrackFromPlaylist(track: Track): Flow<Track> = flow {
        val tracks = appDatabase.trackDao().deleteTrack(track)
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromTrackEntity(track: TrackEntity): Track {
        return trackDbConvertor.map(track)
    }
}
