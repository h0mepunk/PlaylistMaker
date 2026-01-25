package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository): TracksHistoryInteractor {

    override fun getTracksHistory(): ArrayList<Track> {
        return repository.getTracksHistory()
    }

    override fun saveTracksHistory(tracks: ArrayList<Track>) {
        repository.saveTracksHistory(tracks)
    }
}