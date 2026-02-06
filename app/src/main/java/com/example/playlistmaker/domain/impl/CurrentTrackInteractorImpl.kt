package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.api.CurrentTrackRepository
import com.example.playlistmaker.domain.models.Track

class CurrentTrackInteractorImpl(private val repository: CurrentTrackRepository): CurrentTrackInteractor {

    override fun saveCurrentTrack(track: Track) {
        repository.saveCurrentTrack(track)
    }

    override fun getCurrentTrack(): Track {
        return  repository.getCurrentTrack()
    }
}