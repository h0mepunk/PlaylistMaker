package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(
    private val items: List<Track>,
) : RecyclerView.Adapter<TracksViewHolder> () {

    private val trackDataProcesser = TrackDataProcesser()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            val sharedPreferences = holder.itemView
                .getContext()
                .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            val track = items[position]
            var trackHistory = trackDataProcesser
                .tracksListFromJson(
                    sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")!!
                ) as ArrayList<Track>

            if (trackHistory.size == 10) {
                trackHistory.removeAt(0)
                trackHistory.add(0, track)
            }
            if (trackHistory.contains(track)) {
                trackHistory.add(0, track)
                trackHistory.remove(track)
            }

            sharedPreferences.edit()
                .putString(
                    TRACK_HISTORY_LIST_KEY,
                    trackDataProcesser
                        .tracksListToJson(
                            trackHistory as Array<Track>
                        )
                )
                .apply()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}