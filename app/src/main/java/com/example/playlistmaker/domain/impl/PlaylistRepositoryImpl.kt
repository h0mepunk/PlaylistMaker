package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.PlaylistPageState
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.id, playlist.name, playlist.description, playlist.imgUri)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlistEntity = playlistDao.getPlaylistById(playlistId)
        emit(playlistEntity.let { playlistDbConverter.map(it) })
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, track: Track) {
        playlistDao.addTrackToPlaylist(playlistId, track.trackId.toString(), track.trackTime.trackTimeToLong())
    }

    override fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        var trackList = emptyList<Track>()
        val playlist = playlistDao.getPlaylistById(playlistId)
        if (!playlist.tracks.isNullOrEmpty()) {
            playlistDao.getPlaylistById(playlistId).tracks!!.split(",").forEach {
                trackId ->
                Log.i("PlaylistRepositoryImpl", "search track by id: $trackId")
                trackList =
                    trackList.plus(
                        dbConverter.map(
                            playlistsTracksDao.getTrackById(
                                trackId.toInt()
                            )
                        )
                    )
            }
        }
        emit(trackList.reversed())
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, track: Track) {
        playlistDao.deleteTrackFromPlaylist(playlistId, track.trackId, track.trackTime.trackTimeToLong())
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun insertTrack(track: Track) {
        val entity = dbConverter.map(track)
        playlistsTracksDao.insertTrack(entity)
    }

    override fun getTrackById(id: Int): Flow<Track> = flow {
        val entity = playlistsTracksDao.getTrackById(id)
        emit(entity.let { dbConverter.map(it) })
    }

    private fun trackTimeToString(trackTime: Long): String {
        val minutes = trackTime / 60000
        val seconds = (trackTime % 60000) / 1000
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun String.trackTimeToLong(): Long {
        val parts = this.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return minutes * 60000 + seconds * 1000
    }
}
