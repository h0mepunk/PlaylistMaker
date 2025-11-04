package com.example.playlistmaker.data

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer): MediaPlayerRepository {

    private var playerState = STATE_DEFAULT


    override fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        Log.e("MediaPlayer","preparing player with url $url")
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
           onPrepared()
            Log.e("MediaPlayer","player prepared, url $url")
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion()
            Log.e("MediaPlayer","player completed")
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            Log.e("MediaPlayer", "MediaPlayer error: what=$what, extra=$extra")
            true
        }
    }

    override fun startPlayer(
        onPlaying: () -> Unit
    ) {
        mediaPlayer.start()
        onPlaying()
        playerState = STATE_PLAYING
        Log.e("MediaPlayer","player started")
    }

    override fun pausePlayer(
        onPause: () -> Unit
    ) {
        mediaPlayer.pause()
        onPause()
        playerState = STATE_PAUSED
        Log.e("MediaPlayer","player paused")
    }

    override fun stopPlayer(
        onStop: () -> Unit
    ) {
        mediaPlayer.stop()
        onStop()
        playerState = STATE_PREPARED
        Log.e("MediaPlayer","player stopped")
    }

    override fun getPlayerState(): Int {
        Log.e("MediaPlayer","player state: $playerState")
        return playerState
    }

    override fun updateTimer(
        onUpdate: () -> Unit
    ) {
        if(playerState == STATE_PLAYING) {
            Log.e("MediaPlayer","update timer")
            onUpdate()
        }
    }

    override fun playbackControl(
        start: () -> Unit,
        pause: () -> Unit
    ) {
        when(playerState) {
            STATE_PLAYING -> {
                Log.e("MediaPlayer","player paused playback")
                pause()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                Log.e("MediaPlayer","player started playback")
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