package com.example.playlistmaker.ui.playlist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.models.Track

class TracksPlaylistPageAdapter(
    private val currentTrackInteractor: CurrentTrackInteractor,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
): RecyclerView.Adapter<TracksPlaylistPageAdapter.PlaylistPageViewHolder> () {

    private lateinit var context: Context
    var items: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistPageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        context = parent.context
        return PlaylistPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistPageViewHolder, position: Int) {
        Log.i(LOG_TAG, "onBindViewHolder")
        holder.bind(items[position])

        Log.i(LOG_TAG, "onBindViewHolder items $items")

        holder.itemView.setOnClickListener {
            val track = items[position]
            currentTrackInteractor.saveCurrentTrack(track)
            onTrackClick(track)
        }

        holder.itemView.setOnLongClickListener {
            val track = items[position]
            onTrackLongClick(track)
            true
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PlaylistPageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

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

    companion object {
        private const val LOG_TAG = "PlaylistPageAdapter"
    }
}