package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
class PlaylistEntity (
    @PrimaryKey
    val id: Int,
    val name: String,
    val imgUrl100: String,
    val tracks: String,
    val previewUrl: String
)