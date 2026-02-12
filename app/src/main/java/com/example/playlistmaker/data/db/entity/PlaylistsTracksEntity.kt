package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_tracks_table")
class PlaylistsTracksEntity (
        @PrimaryKey
        val id: Int,
        val trackName: String,
        val artistName: String,
        val trackTime: String,
        val artworkUrl100: String,
        val releaseDate: String?,
        val primaryGenreName: String,
        val country: String,
        val collectionName: String,
        val previewUrl: String,
        val addedAt: Long = System.currentTimeMillis()
)