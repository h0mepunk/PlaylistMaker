package com.example.playlistmaker

import android.util.Log
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
                .context
                .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            val track = items[position]
            val trackHistory = trackDataProcesser
                .tracksListFromJson(
                    sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")!!
                )

            if (trackHistory.size == 10) {
                trackHistory.removeAt(9)
                trackHistory.add(0, track)
                Log.e("?????? TrackAdapter", "track history 10: ${ trackDataProcesser.tracksListToJson(trackHistory)}")
            }
            if (trackHistory.contains(track)) {
                trackHistory.remove(track)
                trackHistory.add(0, track)
                Log.e("?????? TrackAdapter", "track history povtorka: ${ trackDataProcesser.tracksListToJson(trackHistory)}")
            }
            else {
                trackHistory.add(0, track)
                Log.e("?????? TrackAdapter", "track history: ${ trackDataProcesser.tracksListToJson(trackHistory)}") }

            sharedPreferences.edit()
                .putString(
                    TRACK_HISTORY_LIST_KEY,
                    trackDataProcesser.tracksListToJson(trackHistory)
                )
                .apply()
            Log.e("?????? TrackAdapter", "track history saved: ${ trackDataProcesser.tracksListToJson(trackHistory)}")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}