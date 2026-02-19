package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistsTracksDao: PlaylistTracksDao,
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val dbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = playlistDao.getPlaylists()
        var mappedPlaylists: List<Playlist>  = emptyList()
        playlist.forEach { it ->
            playlistDbConverter.map(it)
            mappedPlaylists = mappedPlaylists.plus(playlistDbConverter.map(it))
        }
        emit(mappedPlaylists)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylist(playlistEntity)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlistEntity = playlistDao.getPlaylistById(playlistId)
        emit(playlistEntity.let { playlistDbConverter.map(it) })
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: String) {
        playlistDao.addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun insertTrack(track: Track) {
        val entity = dbConverter.map(track)
        playlistsTracksDao.insertTrack(entity)
    }

    override fun getTrackById(id: Int): Flow<Track> = flow {
        val entity = playlistsTracksDao.getTrackById(id)
        emit(entity.let { dbConverter.map(it) })
    }
}
