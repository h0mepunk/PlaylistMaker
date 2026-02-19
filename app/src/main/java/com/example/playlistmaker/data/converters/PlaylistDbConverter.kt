package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.tracks,
            playlist.tracksCount,
            playlist.imgUri
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.name,
            playlist.description,
            playlist.previewUri,
            playlist.id,
            playlist.tracksCount,
            playlist.tracks
        )
    }
}