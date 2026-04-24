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
import com.example.playlistmaker.Const.SONG_ARTIST_KEY
import com.example.playlistmaker.Const.SONG_NAME_KEY
import com.example.playlistmaker.Const.SONG_URL_KEY
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.track.TrackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var songName = ""
    private var songArtist = ""

    private var playerState: TrackState = TrackState.Init(songUrl)

    private val binder = MusicServiceBinder()

    private var timerJob: Job? = null

    private var playerReleased = false

    private var isInForeground = false

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer.isPlaying) {
                delay(250L)
                playerState = TrackState.Playing(getCurrentPlayerPosition())
                playerStateListener?.onStateChanged(playerState)
            }
        }
    }

    fun updateTimer() {

    }

    fun setPlayerStateListener(listener: PlayerStateListener) {
        playerStateListener = listener
    }

    fun getCurrentPlayerPosition(): String {
        val ms = try {
            mediaPlayer.currentPosition
        } catch (e: IllegalStateException) {
            0
        }
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
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
            timerJob?.cancel()
            playerState = TrackState.Stopped
            playerStateListener?.onStateChanged(playerState)
            Log.i(LOG_TAG, "player completed")
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
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                timerJob?.cancel()
                playerState = TrackState.Paused(getCurrentPlayerPosition())
                playerStateListener?.onStateChanged(playerState)
                Log.i(LOG_TAG,"player paused")
            }
    }

    override fun onBind(intent: Intent?): IBinder? {
        initPlayer(intent)

        return binder
    }

    fun startForeground(intent: Intent) {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
        isInForeground = true
    }

    fun stopForeground() {
        if (isInForeground) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            isInForeground = false
        }
    }

    private fun initPlayer(intent: Intent?) {
        songUrl = intent?.getStringExtra(SONG_URL_KEY) ?: ""
        songName = intent?.getStringExtra(SONG_NAME_KEY) ?: ""
        songArtist = intent?.getStringExtra(SONG_ARTIST_KEY) ?: ""

        playerState = TrackState.Init(songUrl)
        preparePlayer(songUrl)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isInForeground) {
            val notification = createServiceNotification()
            if (notification != null) {
                startForeground(SERVICE_NOTIFICATION_ID, notification)
                isInForeground = true
            } else {
                Log.e(LOG_TAG, "Notification is null, cannot start foreground in onStartCommand")
            }
        }
        initPlayer(intent)

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
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

    fun reset() {
        mediaPlayer.reset()
    }

    fun releasePlayer() {
        if (playerReleased) return
        timerJob?.cancel()
        try {
            mediaPlayer.stop()
        } catch (e: IllegalStateException) {
            Log.w(LOG_TAG, "releasePlayer: stop skipped (${e.message})")
        }
        playerState = TrackState.Init(songUrl)
        playerStateListener?.onStateChanged(playerState)
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.release()
        playerReleased = true
    }

    fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText("$songArtist - $songName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
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

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    interface PlayerStateListener {
        fun onStateChanged(state: TrackState)
    }
}