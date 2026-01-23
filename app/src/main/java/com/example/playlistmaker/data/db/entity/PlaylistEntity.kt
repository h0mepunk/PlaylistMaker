package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.models.Track

@Entity(tableName = "playlists_table")
class PlaylistEntity (
    @PrimaryKey
    val id: Int,
    val name: String,
    val imgUrl100: String,
    val tracks: List<Track>,
    val previewUrl: String
)