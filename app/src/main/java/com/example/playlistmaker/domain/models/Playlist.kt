package com.example.playlistmaker.domain.models

data class Playlist(
    val name: String,
    val description: String,
    val imgUri: String,
    val id: Int,
    val tracksCount: Int = tracks.split(",").size,
    val tracks: String,
)