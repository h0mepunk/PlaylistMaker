package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.models.Track

class MediaPlayerInteractorImpl(private val repository: MediaPlayerRepository): MediaPlayerInteractor {

    override fun clickLike(track: Track) {
        repository.clickLike(track)
    }
}