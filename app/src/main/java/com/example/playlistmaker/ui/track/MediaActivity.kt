package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.track.TrackView
import com.example.playlistmaker.presentation.track.TrackPresenter
import com.example.playlistmaker.ui.track.model.TrackState
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MediaActivity : MvpAppCompatActivity(), TrackView {
    private lateinit var toolbar : Toolbar
    private lateinit var playButton : Button
    private lateinit var trackTime : TextView
    private lateinit var placeholderImage: ImageView

    private val trackPresenter by moxyPresenter {
        Creator.provideTrackPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        toolbar = findViewById(R.id.media_toolbar)
        playButton = findViewById(R.id.media_button_play)
        trackTime = findViewById(R.id.media_track_length)
        placeholderImage = findViewById(R.id.media_track_cover)
        val trackTitle = findViewById<TextView>(R.id.media_track_title)
        val trackArtist = findViewById<TextView>(R.id.media_track_artist)
        val trackAlbum = findViewById<TextView>(R.id.media_info_album_value)
        val trackGenre = findViewById<TextView>(R.id.media_info_genre_value)
        val trackReleaseDate = findViewById<TextView>(R.id.media_info_year_value)
        val trackDuration = findViewById<TextView>(R.id.media_info_length_value)
        val trackCountry = findViewById<TextView>(R.id.media_info_country_value)

        trackPresenter.onCreate()

        trackTitle.text = trackPresenter.currentTrack.trackName
        trackArtist.text = trackPresenter.currentTrack.artistName
        trackAlbum.text = trackPresenter.currentTrack.collectionName
        trackGenre.text = trackPresenter.currentTrack.primaryGenreName
        trackReleaseDate.text = trackPresenter.currentTrack.releaseDate
        trackDuration.text = trackPresenter.currentTrack.trackTime
        trackCountry.text = trackPresenter.currentTrack.country

        playButton.setOnClickListener {
            Log.e("TrackActivity","play/stop button clicked")
            trackPresenter.playbackControl()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        trackPresenter.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackPresenter.onDestroy()
    }

    override fun showCover(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.media_cover_preview)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(placeholderImage)
    }

    override fun enablePlayButton(enabled: Boolean) {
        playButton.isEnabled = enabled
    }

    override fun render(state: TrackState) {
        when (state) {
            is TrackState.Paused -> {
                playButton.setBackgroundResource(R.drawable.media_play)
            }
            is TrackState.Playing -> {
                playButton.setBackgroundResource(R.drawable.media_stop)
                trackTime.text = state.trackTime?: getString(R.string.start_time_zero)
            }
            is TrackState.Stopped -> {
                playButton.setBackgroundResource(R.drawable.media_play)
                trackTime.text = getString(R.string.start_time_zero)
            }
        }
    }

}