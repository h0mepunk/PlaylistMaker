package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.track.TrackState
import com.example.playlistmaker.presentation.track.TrackViewModel

class TrackActivity : AppCompatActivity() {
    private lateinit var toolbar : Toolbar
    private lateinit var playButton : Button
    private lateinit var trackTime : TextView
    private lateinit var placeholderImage: ImageView

    private var viewModel: TrackViewModel? = null

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

        viewModel = ViewModelProvider(this, TrackViewModel.getFactory())[TrackViewModel::class.java]
        viewModel?.onCreate()

        viewModel?.observeState()?.observe(this) {
            render(it)
        }

        trackTitle.text = viewModel?.currentTrack?.trackName
        trackArtist.text = viewModel?.currentTrack?.artistName
        trackAlbum.text = viewModel?.currentTrack?.collectionName
        trackGenre.text = viewModel?.currentTrack?.primaryGenreName
        trackReleaseDate.text = viewModel?.currentTrack?.releaseDate
        trackDuration.text = viewModel?.currentTrack?.trackTime
        trackCountry.text = viewModel?.currentTrack?.country

        playButton.setOnClickListener {
            Log.e("TrackActivity","play/stop button clicked")
            viewModel?.playbackControl()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel?.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    fun setPlayButtonActive(active: Boolean) {
//        val resId = if (active) {
//            R.drawable.media_play
//        } else {
//            R.drawable.media_stop
//        }
//        playButton.setBackgroundResource(resId)
//        playButton.background = AppCompatResources.getDrawable(this, resId)
//    }

    fun setTrackTimeText(text: String) {
        trackTime.text = text
    }

    fun showCover(url: String) {
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

    fun render(state: TrackState) {
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
            is TrackState.Init -> {
                showCover(state.previewImgUrl)
                playButton.setBackgroundResource(R.drawable.media_play)
                trackTime.text = getString(R.string.start_time_zero)
            }
            is TrackState.Prepared -> { playButton.isEnabled = true }
        }
    }


}