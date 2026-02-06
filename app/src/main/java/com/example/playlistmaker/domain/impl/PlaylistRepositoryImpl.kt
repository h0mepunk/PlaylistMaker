package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
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
}
