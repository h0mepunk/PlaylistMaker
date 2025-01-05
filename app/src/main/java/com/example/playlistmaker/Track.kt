package com.example.playlistmaker

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String
)

data class TrackResponse(
    val results: List<Track>,
    val resultCount: Int
)