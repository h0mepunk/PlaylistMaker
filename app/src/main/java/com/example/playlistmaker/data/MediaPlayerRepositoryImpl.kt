package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl: MediaPlayerRepository {

    private var playerState = STATE_DEFAULT

    private var mediaPlayer = MediaPlayer()

    override fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
           onPrepared()
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
            playerState = STATE_PREPARED
        }
    }

    override fun startPlayer(
        onPlaying: () -> Unit
    ) {
        mediaPlayer.start()
        onPlaying()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer(
        onPause: () -> Unit
    ) {
        mediaPlayer.pause()
        onPause()
        playerState = STATE_PAUSED
    }

    override fun stopPlayer(
        onStop: () -> Unit
    ) {
        mediaPlayer.stop()
        onStop()
        playerState = STATE_PREPARED
    }

    override fun getPlayerState(): Int {
        return playerState
    }

    override fun updateTimer(
        onUpdate: () -> Unit
    ) {
        if(playerState == STATE_PLAYING) {
            onUpdate()
        }
    }

    override fun playbackControl(
        start: () -> Unit,
        pause: () -> Unit
    ) {
        when(playerState) {
            STATE_PLAYING -> {
                pause()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                start()
            }
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

}