package com.example.playlistmaker.presentation.track

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.track.model.TrackState
import com.example.playlistmaker.util.Creator
import moxy.InjectViewState
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.Locale

@InjectViewState
class TrackPresenter(
    private val context: Context,
): MvpPresenter<TrackView>() {

    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
    private val mediaPlayer = Creator.provideMediaPlayerInteractor().getMediaPlayer()
    lateinit var currentTrack: Track
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable = Runnable { updateTimer() }

    private fun updateTimer() {
        mediaPlayerInteractor.updateTimer (
            onUpdate = {
                renderState(
                    TrackState.Playing(
                        SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                    )
                )
                handler.postDelayed(timerRunnable, 250)
            }
        )
    }

    private fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url,
            onPrepared = {
                viewState.enablePlayButton(true)
            },
            onCompletion = {
                renderState(TrackState.Playing(null))
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer(
            onPlaying = {
                renderState(TrackState.Playing(null))
                handler.post(timerRunnable)
            }
        )
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer(
            onPause = {
                renderState(TrackState.Paused)
                handler.removeCallbacks(timerRunnable)
            }
        )
    }

    fun stopPlayer() {
        mediaPlayerInteractor.stopPlayer (
            onStop = {
                renderState(TrackState.Stopped)
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

        viewState.showCover(currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))

        mediaPlayer.setOnCompletionListener {
            Log.e("TrackController","player completed")
            stopPlayer()
        }

        preparePlayer(currentTrack.previewUrl)
    }

    private fun renderState(state: TrackState) {
        viewState.render(state)
    }

    override fun onDestroy() {
        mediaPlayer.reset()
    }
}