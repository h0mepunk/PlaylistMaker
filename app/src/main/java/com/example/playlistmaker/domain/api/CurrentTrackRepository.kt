package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface CurrentTrackRepository {
    fun saveCurrentTrack(track: Track)

    fun getCurrentTrack(): Track
}