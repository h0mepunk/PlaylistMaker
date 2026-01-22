package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.models.Track

class MediaPlayerInteractorImpl(private val repository: MediaPlayerRepository): MediaPlayerInteractor {

    override fun getPlayerState(): Int {
        return repository.getPlayerState()
    }

    override fun pausePlayer(onPause: () -> Unit) {
        repository.pausePlayer(onPause)
    }

    override fun stopPlayer(onStop: () -> Unit) {
        repository.stopPlayer(onStop)
    }

    override fun startPlayer(onPlaying: () -> Unit) {
        repository.startPlayer(onPlaying)
    }

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun updateTimer(onUpdate: () -> Unit) {
        repository.updateTimer(onUpdate)
    }

    override fun clickLike(track: Track) {
        repository.clickLike(track)
    }
}