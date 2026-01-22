package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_table")
class TrackEntity (
    @PrimaryKey
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val trackId: Int,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String,
    val previewUrl: String
)