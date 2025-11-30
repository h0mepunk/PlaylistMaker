package com.example.playlistmaker.domain.models

data class Playlist(
    val name: String,
    val imgUrl100: String,
    val id: Int,
    val tracks: List<Track>,
    val previewUrl: String
)