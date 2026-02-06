package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface CurrentTrackInteractor {
    fun saveCurrentTrack(track: Track)

    fun getCurrentTrack(): Track
}