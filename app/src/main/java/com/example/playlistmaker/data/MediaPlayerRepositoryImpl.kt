package com.example.playlistmaker.data

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.models.Track

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
        Log.i("MediaPlayer","preparing player with url $url")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
           onPrepared()
            Log.i("MediaPlayer","player prepared, url $url")
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion()
            Log.i("MediaPlayer","player completed")
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            Log.i("MediaPlayer", "MediaPlayer error: what=$what, extra=$extra")
            true
        }
    }

    override fun startPlayer(
        onPlaying: () -> Unit
    ) {
        mediaPlayer.start()
        onPlaying()
        playerState = STATE_PLAYING
        Log.i("MediaPlayer","player started")
    }

    override fun pausePlayer(
        onPause: () -> Unit
    ) {
        mediaPlayer.pause()
        onPause()
        playerState = STATE_PAUSED
        Log.i("MediaPlayer","player paused")
    }

    override fun stopPlayer(
        onStop: () -> Unit
    ) {
        mediaPlayer.stop()
        onStop()
        playerState = STATE_PREPARED
        Log.i("MediaPlayer","player stopped")
    }

    override fun getPlayerState(): Int {
        Log.i("MediaPlayer","player state: $playerState")
        return playerState
    }

    override fun updateTimer(
        onUpdate: () -> Unit
    ) {
        if(playerState == STATE_PLAYING) {
            Log.i("MediaPlayer","update timer")
            onUpdate()
        }
    }

    override fun clickLike(currentTrack: Track) {
        Log.i("MediaPlayer","like clicked for track: ${currentTrack.trackName}")
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

}