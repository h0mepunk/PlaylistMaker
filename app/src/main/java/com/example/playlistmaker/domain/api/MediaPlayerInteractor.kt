package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface MediaPlayerInteractor {

    fun clickLike(track: Track)
}