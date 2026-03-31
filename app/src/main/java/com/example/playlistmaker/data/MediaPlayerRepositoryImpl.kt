package com.example.playlistmaker.data

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.services.track.MusicService

class MediaPlayerRepositoryImpl(): MediaPlayerRepository {

    private var playerState = STATE_DEFAULT


//    override fun getMediaPlayer(): MediaPlayer {
//        return mediaPlayer
//    }

//    override fun preparePlayer(
//        url: String,
//        onPrepared: () -> Unit,
//        onCompletion: () -> Unit
//    ) {
//        Log.i(LOG_TAG,"preparing player with url $url")
//        mediaPlayer.reset()
//        mediaPlayer.setDataSource(url)
//        mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener {
//            playerState = STATE_PREPARED
//           onPrepared()
//            Log.i(LOG_TAG,"player prepared, url $url")
//        }
//        mediaPlayer.setOnCompletionListener {
//            playerState = STATE_PREPARED
//            onCompletion()
//            Log.i(LOG_TAG,"player completed")
//        }
//        mediaPlayer.setOnErrorListener { _, what, extra ->
//            Log.i(LOG_TAG, "error: what=$what, extra=$extra")
//            true
//        }
//    }

//    override fun startPlayer(
//        onPlaying: () -> Unit
//    ) {
//
//        //mediaPlayer.start()
//        onPlaying()
////        musicService.startForeground(1, null)
//        playerState = STATE_PLAYING
//        Log.i(LOG_TAG,"player started")
//    }

//    override fun pausePlayer(
//        onPause: () -> Unit
//    ) {
//        mediaPlayer.pause()
//        onPause()
//        playerState = STATE_PAUSED
//        Log.i(LOG_TAG,"player paused")
//    }
//
//    override fun stopPlayer(
//        onStop: () -> Unit
//    ) {
//        mediaPlayer.stop()
//        onStop()
//        playerState = STATE_PREPARED
//        Log.i(LOG_TAG,"player stopped")
//    }
//
//    override fun getPlayerState(): Int {
//        Log.i(LOG_TAG,"player state: $playerState")
//        return playerState
//    }
//
//    override fun updateTimer(
//        onUpdate: () -> Unit
//    ) {
//        if(playerState == STATE_PLAYING) {
//            Log.i(LOG_TAG,"update timer")
//            onUpdate()
//        }
//    }

    override fun clickLike(currentTrack: Track) {
        Log.i(LOG_TAG,"like clicked for track: ${currentTrack.trackName}")
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val LOG_TAG = "MediaPlayer"
    }

}