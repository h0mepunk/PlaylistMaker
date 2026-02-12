package com.example.playlistmaker.ui.track

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist

class PlaylistsAdapterMedia(
    private val onPlaylistClick: (Playlist) -> Unit
): RecyclerView.Adapter<PlaylistsAdapterMedia.PlaylistsViewHolder> () {

    private lateinit var context: Context
    var items: List<Playlist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        context = parent.context
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        Log.i(LOG_TAG, "onBindViewHolder")
        holder.bind(items[position])

        Log.i(LOG_TAG, "onBindViewHolder items $items")

        holder.itemView.setOnClickListener {
            val playlist = items[position]
            onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PlaylistsViewHolder(
        itemView: View
    )
        : RecyclerView.ViewHolder(itemView) {

        private val playlistTitle: TextView = itemView.findViewById(R.id.song_name)
        private val playlistSubtitle: TextView = itemView.findViewById(R.id.song_author)
        private val playlistImage: ImageView = itemView.findViewById(R.id.song_album_cover)

        private val dot: ImageView = itemView.findViewById(R.id.song_dot)

        private val arrow = itemView.findViewById<ImageView>(R.id.song_arrow)

        private val time: TextView = itemView.findViewById(R.id.song_time)

        fun bind(item: Playlist) {
            val size = itemView.context.resources.getDimensionPixelSize(R.dimen.playlist_cover_size)
            Glide.with(itemView.context)
                .load(item.imgUri)
                .placeholder(R.drawable.placeholder)
                .override(size, size)
                .centerCrop()
                .transform(RoundedCorners(itemView.context.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()))
                .into(playlistImage)

            playlistTitle.text = item.name
            playlistSubtitle.text = item.tracksCount.toString() + " треков"
            time.visibility = View.INVISIBLE
            dot.visibility = View.INVISIBLE
            arrow.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val LOG_TAG = "PlaylistsListMediaAdapter"
    }
}