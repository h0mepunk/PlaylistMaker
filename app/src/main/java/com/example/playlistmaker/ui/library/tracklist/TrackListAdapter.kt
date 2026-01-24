package com.example.playlistmaker.ui.library.tracklist

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackListAdapter(
    private val onTrackClick: (Track) -> Unit
): RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> () {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var context: Context
    var items: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        sharedPreferences = parent.context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        context = parent.context
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        Log.i("TrackListViewHolder", "onBindViewHolder")
        holder.bind(items[position])

        Log.i("TrackListViewHolder", "onBindViewHolder items ${items}")

        holder.itemView.setOnClickListener {
            val track = items[position]
            onTrackClick(track)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TrackListViewHolder(
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

            songDuration.text = item.trackTime
            songTitle.text = item.trackName
            songSubtitle.text = item.artistName
        }
    }
}