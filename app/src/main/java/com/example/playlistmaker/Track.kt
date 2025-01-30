package com.example.playlistmaker

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int
)

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)