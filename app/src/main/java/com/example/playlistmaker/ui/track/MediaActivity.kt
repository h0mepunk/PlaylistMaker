package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.track.TrackView
import com.example.playlistmaker.presentation.track.TrackPresenter

class MediaActivity : AppCompatActivity(), TrackView {
    private lateinit var toolbar : Toolbar
    private lateinit var playButton : Button
    private lateinit var trackTime : TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var trackPresenter : TrackPresenter

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

        trackPresenter = Creator.provideTrackController(this, this)
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

    override fun setPlayButtonActive(active: Boolean) {
        val resId = if (active) {
            R.drawable.media_play
        } else {
            R.drawable.media_stop
        }
        playButton.setBackgroundResource(resId)
        playButton.background = AppCompatResources.getDrawable(this, resId)
    }

    override fun getPlaceholderImageView(): ImageView {
        return placeholderImage
    }

    override fun setTrackTimeText(text: String) {
        trackTime.text = text
    }

    override fun enablePlayButton(enabled: Boolean) {
        playButton.isEnabled = enabled
    }

}