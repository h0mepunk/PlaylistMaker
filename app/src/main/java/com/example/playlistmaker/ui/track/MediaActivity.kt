package com.example.playlistmaker.ui.track

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TrackManager
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private val toolbar by lazy { findViewById<Toolbar>(R.id.media_toolbar)}
    private val playButton by lazy { findViewById<Button>(R.id.media_button_play)}
    private val trackTime by lazy { findViewById<TextView>(R.id.media_track_length)}
    val sharedPreferences by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)}
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

    private fun updateTimer() {
        if(playerState == STATE_PLAYING) {
            trackTime.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
            handler.postDelayed(timerRunnable, 250)
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.background = getDrawable(R.drawable.media_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.background = getDrawable(R.drawable.media_stop)
        handler.post(timerRunnable)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.background = getDrawable(R.drawable.media_play)
        handler.removeCallbacks(timerRunnable)
        playerState = STATE_PAUSED
    }

    private fun stopPlayer() {
        mediaPlayer.stop()
        playButton.background = getDrawable(R.drawable.media_play)
        trackTime.text = "0:00"
        playerState = STATE_PREPARED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val trackManager = TrackManager(this)
        val currentTrack = trackManager.getCurrentTrack()
        val placeholderImage: ImageView = findViewById(R.id.media_track_cover)
        val trackTitle = findViewById<TextView>(R.id.media_track_title)
        val trackArtist = findViewById<TextView>(R.id.media_track_artist)
        val trackAlbum = findViewById<TextView>(R.id.media_info_album_value)
        val trackGenre = findViewById<TextView>(R.id.media_info_genre_value)
        val trackReleaseDate = findViewById<TextView>(R.id.media_info_year_value)
        val trackDuration = findViewById<TextView>(R.id.media_info_length_value)
        val trackCountry = findViewById<TextView>(R.id.media_info_country_value)

        Glide.with(this)
            .load(currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.media_cover_preview)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(placeholderImage)

        trackTitle.text = currentTrack.trackName
        trackArtist.text = currentTrack.artistName
        trackAlbum.text = currentTrack.collectionName
        trackGenre.text = currentTrack.primaryGenreName
        trackReleaseDate.text = currentTrack.releaseDate
        trackDuration.text = currentTrack.trackTimeMillis
        trackCountry.text = currentTrack.country

        toolbar.setNavigationOnClickListener {
            finish()
        }

        preparePlayer(currentTrack.previewUrl)

        playButton.setOnClickListener {
            playbackControl()
        }

        mediaPlayer.setOnCompletionListener {
            stopPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}