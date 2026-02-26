package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "playlists_table")
class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val tracks: String?,
    val tracksCount: Int,
    val previewUri: String,
    val timestamp: Long= System.currentTimeMillis(),
    val timeTotal: Long = 0L
)