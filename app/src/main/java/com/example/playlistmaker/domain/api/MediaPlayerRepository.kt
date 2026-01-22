package com.example.playlistmaker.domain.api

import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.Track

interface MediaPlayerRepository {

    fun getMediaPlayer(): MediaPlayer

    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)

    fun startPlayer(onPlaying: () -> Unit)

    fun pausePlayer(onPause: () -> Unit)

    fun stopPlayer(onStop: () -> Unit)

    fun getPlayerState(): Int

    fun updateTimer(onUpdate: () -> Unit)

    fun clickLike(currentTrack: Track)
}