package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {

    fun getTracksHistory(): ArrayList<Track>

    fun saveTracksHistory(tracks: ArrayList<Track>)

    fun saveCurrentTrack(track: Track)

    fun getCurrentTrack(): Track

    interface TracksHistoryConsumer {
        fun consume(history: ArrayList<Track>)
    }
}