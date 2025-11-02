package com.example.playlistmaker.presentation.track

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.track.TrackView
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class TrackPresenter(
    val view: TrackView,
    private val context: Context,
) {

    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
    private val mediaPlayer = Creator.provideMediaPlayerInteractor().getMediaPlayer()
    lateinit var currentTrack: Track
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

    private fun updateTimer() {
        mediaPlayerInteractor.updateTimer (
            onUpdate = {
                view.setTrackTimeText(SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition))
                handler.postDelayed(timerRunnable, 250)
            }
        )
    }

    private fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url,
            onPrepared = {
                view.enablePlayButton(true)
            },
            onCompletion = {
                view.setPlayButtonActive(true)
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer(
            onPlaying = {
                view.setPlayButtonActive(false)
                handler.post(timerRunnable)
            }
        )
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                view.setPlayButtonActive(true)
                handler.removeCallbacks(timerRunnable)
            }
        )
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer (
            onStop = {
                view.setPlayButtonActive(true)
                view.setTrackTimeText(context.getString(R.string.start_time_zero))
            }
        )
    }

    fun playbackControl() {
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
        currentTrack = tracksHistoryInteractor.getCurrentTrack()

        Glide.with(context)
            .load(currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.media_cover_preview)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        context.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(view.getPlaceholderImageView())


        mediaPlayer.setOnCompletionListener {
            Log.e("TrackController","player completed")
            stopPlayer()
        }

        preparePlayer(currentTrack.previewUrl)
    }

    fun onDestroy() {
        mediaPlayer.release()
    }
}