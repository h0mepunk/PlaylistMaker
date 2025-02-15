package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY

class TrackHistoryAdapter(
    private var items: List<Track>,
    context: Context
) : RecyclerView.Adapter<TracksViewHolder> () {

    private val trackDataProcessor = TrackDataProcessor()
    private val sharedPreferences = context
        .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        items = trackDataProcessor.getTracksList(sharedPreferences)
        holder.bind(items[position])
        var itemsList = items as ArrayList<Track>

        holder.itemView.setOnClickListener {
            val track = items[position]
            if (itemsList.size == 10) {
                itemsList.removeAt(9)
                itemsList.add(0, track)
            }
            if (itemsList.contains(track)) {
                itemsList.remove(track)
                itemsList.add(0, track)
            }
            else {
                itemsList.add(0, track)
            }
            sharedPreferences.edit()
                .putString(
                    TRACK_HISTORY_LIST_KEY,
                    trackDataProcessor.tracksListToJson(itemsList)
                )
                .apply()
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}