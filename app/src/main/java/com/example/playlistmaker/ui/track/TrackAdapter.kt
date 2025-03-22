package com.example.playlistmaker.ui.track

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.TrackManager
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter: RecyclerView.Adapter<TrackAdapter.TracksViewHolder> () {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var trackManager : TrackManager
    private lateinit var context: Context
    var items: List<Track> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        trackManager = TrackManager(parent.context)
        sharedPreferences = parent.context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        context = parent.context
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        Log.e("????", "onBindViewHolder")
        holder.bind(items[position])

        Log.e("????", "onBindViewHolder items ${items}")

        holder.itemView.setOnClickListener {
            if(clickDebounce()) {
                val track = items[position]
                var trackHistory = trackManager.getTracksList(sharedPreferences)
                if (trackHistory.size == 10) {
                    trackHistory.removeAt(9)
                    trackHistory.add(0, track)
                }
                if (trackHistory.contains(track)) {
                    trackHistory.remove(track)
                    trackHistory.add(0, track)
                }
                else {
                    trackHistory.add(0, track)
                }
                trackManager.saveTracksList(trackHistory)
                trackManager.saveCurrentTrack(track)

                val mediaActivity = Intent(context, MediaActivity::class.java)
                context.startActivity(mediaActivity)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    class TracksViewHolder(
        itemView: View
    )
        : RecyclerView.ViewHolder(itemView) {

        private val songTitle: TextView = itemView.findViewById(R.id.song_name)
        private val songSubtitle: TextView = itemView.findViewById(R.id.song_author)
        private val songImage: ImageView = itemView.findViewById(R.id.song_album_cover)
        private val songDuration: TextView = itemView.findViewById(R.id.song_time)

        fun bind(item: Track) {
            Glide.with(itemView.context)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .into(songImage)

            songDuration.text = item.trackTimeMillis
            songTitle.text = item.trackName
            songSubtitle.text = item.artistName
        }
    }
}