package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.Const.CURRENT_TRACK_KEY
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}
    val sharedPreferences by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)}
    val trackDataProcessor = TrackDataProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val currentTrack = trackDataProcessor.trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, ""))
        val placeholderImage: ImageView = findViewById(R.id.media_track_cover)
        val trackTitle = findViewById<TextView>(R.id.media_track_title)
        val trackArtist = findViewById<TextView>(R.id.media_track_artist)
        val trackAlbum = findViewById<TextView>(R.id.media_info_album_value)
        val trackGenre = findViewById<TextView>(R.id.media_info_genre_value)
        val trackReleaseDate = findViewById<TextView>(R.id.media_info_year_value)
        val trackDuration = findViewById<TextView>(R.id.media_info_length)
        val trackCountry = findViewById<TextView>(R.id.media_info_country_value)

        Glide.with(this)
            .load(currentTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.media_cover_preview)
            .into(placeholderImage)

        trackTitle.text = currentTrack.trackName
        trackArtist.text = currentTrack.artistName
        trackAlbum.text = currentTrack.collectionName
        trackGenre.text = currentTrack.primaryGenreName
        trackReleaseDate.text = SimpleDateFormat("YYYY", Locale.getDefault()).format(currentTrack.trackTimeMillis.toLong())
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis.toLong())
        trackCountry.text = currentTrack.country

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

}