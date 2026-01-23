package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTracksEntity(tracks))
    }

    override fun addTrackToPlaylist(track: Track): Flow<Track> = flow {
        appDatabase.trackDao().insertTrack(track.toTrackEntity())
    }

    override fun removeTrackFromPlaylist(track: Track): Flow<Track> = flow {
        appDatabase.trackDao().deleteTrack(track.toTrackEntity())
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromTrackEntity(track: TrackEntity): Track {
        return trackDbConvertor.map(track)
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> Playlist(
            playlist.name,
            playlist.imgUrl100,
            playlist.id,
            emptyList(),
            playlist.previewUrl
            ) }
    }

    private fun Track.toTrackEntity(): TrackEntity {
        return TrackEntity(
            artistName = this.artistName,
            trackId = this.trackId,
            trackName = this.trackName,
            collectionName = this.collectionName,
            artworkUrl100 = this.artworkUrl100,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl,
            trackTime = this.trackTime
        )
    }
}
