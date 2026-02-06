package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.imgUrl100,
            "",
            playlist.previewUrl
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.name,
            playlist.imgUrl100,
            playlist.id,
            emptyList(),
            playlist.previewUrl
        )
    }
}