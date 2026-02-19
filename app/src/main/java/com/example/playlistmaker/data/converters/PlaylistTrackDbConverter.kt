package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistsTracksEntity
import com.example.playlistmaker.domain.models.Track

class PlaylistTrackDbConverter {
    fun map(track: Track): PlaylistsTracksEntity {
        return PlaylistsTracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl)
    }

    fun map(track: PlaylistsTracksEntity): Track {
        return Track(
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.id,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl
        )
    }
}