package com.example.playlistmaker.presentation

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class TrackController(private val activity: Activity) {

    private lateinit var toolbar :Toolbar
    private lateinit var playButton : Button
    private lateinit var trackTime : TextView
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
                playButton.background = AppCompatResources.getDrawable(
                    activity,
                    R.drawable.media_play
                )
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer(
            onPlaying = {
                playButton.background = AppCompatResources.getDrawable(
                    activity,
                    R.drawable.media_stop
                )
                handler.post(timerRunnable)
            }
        )
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                playButton.background = AppCompatResources.getDrawable(
                    activity,
                    R.drawable.media_play
                )
                handler.removeCallbacks(timerRunnable)
            }
        )
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer (
            onStop = {
                playButton.background = AppCompatResources.getDrawable(
                    activity,
                    R.drawable.media_play
                )
                trackTime.text = activity.getString(R.string.start_time_zero)
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

    fun onCreate() {
        toolbar = activity.findViewById(R.id.media_toolbar)
        playButton = activity.findViewById(R.id.media_button_play)
        trackTime = activity.findViewById(R.id.media_track_length)
    val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
    val currentTrack = tracksHistoryInteractor.getCurrentTrack()
    val placeholderImage: ImageView = activity.findViewById(R.id.media_track_cover)
    val trackTitle = activity.findViewById<TextView>(R.id.media_track_title)
    val trackArtist = activity.findViewById<TextView>(R.id.media_track_artist)
    val trackAlbum = activity.findViewById<TextView>(R.id.media_info_album_value)
    val trackGenre = activity.findViewById<TextView>(R.id.media_info_genre_value)
    val trackReleaseDate = activity.findViewById<TextView>(R.id.media_info_year_value)
    val trackDuration = activity.findViewById<TextView>(R.id.media_info_length_value)
    val trackCountry = activity.findViewById<TextView>(R.id.media_info_country_value)

        Glide.with(activity)
            .load(currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.media_cover_preview)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        activity.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
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
            activity.finish()
        }

        preparePlayer(currentTrack.previewUrl)

        playButton.setOnClickListener {
            Log.e("TrackController","play/stop button clicked")
            playbackControl()
        }

        mediaPlayer.setOnCompletionListener {
            Log.e("TrackController","player completed")
            stopPlayer()
        }
    }
}