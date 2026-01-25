package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun getTracksHistory(): ArrayList<Track>

    fun saveTracksHistory(tracks: ArrayList<Track>)
}