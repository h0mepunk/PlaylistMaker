package com.example.playlistmaker

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String,
    val previewUrl: String
)

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)