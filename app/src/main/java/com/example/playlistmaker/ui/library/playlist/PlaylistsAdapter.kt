package com.example.playlistmaker.ui.library.playlist

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
import com.example.playlistmaker.domain.models.Playlist

class PlaylistsAdapter(
  //  private val onPlaylistClick: (Playlist) -> Unit
): RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder> () {

    private lateinit var context: Context
    var items: List<Playlist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item_view, parent, false)
        context = parent.context
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        Log.i(LOG_TAG, "onBindViewHolder")
        holder.bind(items[position])

        Log.i(LOG_TAG, "onBindViewHolder items $items")

        holder.itemView.setOnClickListener {
            val playlist = items[position]
        //    onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PlaylistsViewHolder(
        itemView: View
    )
        : RecyclerView.ViewHolder(itemView) {

        private val playlistTitle: TextView = itemView.findViewById(R.id.playlist_name)
        private val playlistSubtitle: TextView = itemView.findViewById(R.id.playlist_tracks_count)
        private val playlistImage: ImageView = itemView.findViewById(R.id.playlist_cover)

        fun bind(item: Playlist) {
            Glide.with(itemView.context)
                .load(item.imgUri)
                .placeholder(R.drawable.placeholder)
                .into(playlistImage)

            playlistTitle.text = item.name
            playlistSubtitle.text = item.tracksCount.toString() + " треков"
        }
    }

    companion object {
        private const val LOG_TAG = "TrackListAdapter"
    }
}