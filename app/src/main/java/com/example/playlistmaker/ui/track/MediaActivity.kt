package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private val toolbar by lazy { findViewById<Toolbar>(R.id.media_toolbar)}
    private val playButton by lazy { findViewById<Button>(R.id.media_button_play)}
    private val trackTime by lazy { findViewById<TextView>(R.id.media_track_length)}
    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private var mediaPlayer = Creator.provideMediaPlayerInteractor().getMediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

    private fun updateTimer() {
        mediaPlayerInteractor.updateTimer (
            onUpdate = {
                    trackTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                handler.postDelayed(timerRunnable, 250)
            }
        )
    }

    private fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url,
            onPrepared = {
                    playButton.isEnabled = true
            },
            onCompletion = {
                    playButton.background = getDrawable(R.drawable.media_play)
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer(
            onPlaying = {
                    playButton.background = getDrawable(R.drawable.media_stop)
                handler.post(timerRunnable)
            }
        )
    }

    private fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                    playButton.background = getDrawable(R.drawable.media_play)
                handler.removeCallbacks(timerRunnable)
            }
        )
    }

    private fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer (
            onStop = {
                    playButton.background = getDrawable(R.drawable.media_play)
                    trackTime.text = "0:00"
            }
        )
    }

    private fun playbackControl() {
        mediaPlayerInteractor.playbackControl(
            start = {
                startPlayer()
            },
            pause = {
                pausePlayer()
            }
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
        val currentTrack = tracksHistoryInteractor.getCurrentTrack()
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
        trackDuration.text = currentTrack.trackTime
        trackCountry.text = currentTrack.country

        toolbar.setNavigationOnClickListener {
            finish()
        }

        preparePlayer(currentTrack.previewUrl)

        playButton.setOnClickListener {
            Log.e("?????","play/stop button clicked")
            playbackControl()
        }

        mediaPlayer.setOnCompletionListener {
            Log.e("?????","player completed")
            stopPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("?????","onPause")
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}