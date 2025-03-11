package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Const.CURRENT_TRACK_KEY
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY

class TrackAdapter(
    private val items: List<Track>,
    private val context: Context
) : RecyclerView.Adapter<TracksViewHolder> () {

    private val trackDataProcessor = TrackDataProcessor()
    private val sharedPreferences = context
        .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            if(clickDebounce()) {
                val track = items[position]
                var trackHistory = trackDataProcessor.getTracksList(sharedPreferences)
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
                sharedPreferences.edit()
                    .putString(
                        TRACK_HISTORY_LIST_KEY,
                        trackDataProcessor.tracksListToJson(trackHistory)
                    )
                    .putString(CURRENT_TRACK_KEY, trackDataProcessor.trackToJson(track))
                    .apply()

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
}