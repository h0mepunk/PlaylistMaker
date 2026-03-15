package com.example.playlistmaker.domain.models

data class Playlist(
    val name: String,
    val description: String,
    val imgUri: String,
    val id: Int,
    val tracks: String?,
    val tracksCount: Int = tracks?.split(",")?.size ?: 0,
    val timestamp: Long,
    val timeTotal: Long
)