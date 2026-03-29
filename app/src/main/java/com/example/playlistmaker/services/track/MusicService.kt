package com.example.playlistmaker.services.track

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.track.TrackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService(): Service() {

    companion object {
        const val LOG_TAG = "MusicService"
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private var playerStateListener: PlayerStateListener? = null

    private var songUrl = ""

    private var playerState: TrackState = TrackState.Init(songUrl)

    private val binder = MusicServiceBinder()

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer.isPlaying == true) {
                delay(250L)
                playerState = TrackState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    fun setPlayerStateListener(listener: PlayerStateListener) {
        playerStateListener = listener
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition) ?: "00:00"
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun preparePlayer(url: String) {
        Log.i(LOG_TAG,"preparing player with url $url")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = TrackState.Prepared
            playerStateListener?.onStateChanged(playerState)
            Log.i(LOG_TAG,"player prepared, url $url")
        }
        mediaPlayer.setOnCompletionListener {
            playerState = TrackState.Prepared
            playerStateListener?.onStateChanged(playerState)
            Log.i(LOG_TAG,"player completed")
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            Log.i(LOG_TAG, "error: what=$what, extra=$extra")
            true
        }
    }

    fun startPlayer() {
        mediaPlayer.start()
        startTimer()
        playerState = TrackState.Playing(getCurrentPlayerPosition())
        playerStateListener?.onStateChanged(playerState)
        Log.i(LOG_TAG,"player started")
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState = TrackState.Paused(getCurrentPlayerPosition())
        playerStateListener?.onStateChanged(playerState)
        Log.i(LOG_TAG,"player paused")
    }

//    fun stopPlayer(
//        onStop: () -> Unit
//    ) {
//        mediaPlayer.stop()
//    //    timerJob?.cancel()
//        onStop()
//        playerState = TrackState.Prepared
//        playerStateListener?.onStateChanged(playerState)
//        Log.i(LOG_TAG,"player stopped")
//    }

//    fun clickLike(currentTrack: Track) {
//        Log.i(LOG_TAG,"like clicked for track: ${currentTrack.trackName}")
//    }

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra("song_url") ?: ""

        playerState = TrackState.Init(songUrl)
        preparePlayer(songUrl)

        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }


    override fun onCreate() {
        super.onCreate()

        Log.d(LOG_TAG, "onCreate")
        createNotificationChannel()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
       releasePlayer()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    fun reset() {
        mediaPlayer.reset()
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        timerJob?.cancel()
        playerState = TrackState.Init(songUrl)
        playerStateListener?.onStateChanged(playerState)
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music foreground service")
            .setContentText("Our service is working right now!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    interface PlayerStateListener {
        fun onStateChanged(state: TrackState)
    }
}