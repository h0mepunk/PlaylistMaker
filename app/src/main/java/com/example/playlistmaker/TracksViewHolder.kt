package com.example.playlistmaker

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val songTitle: TextView = itemView.findViewById(R.id.song_name)
    private val songSubtitle: TextView = itemView.findViewById(R.id.song_author)
    private val songImage: ImageView = itemView.findViewById(R.id.song_album_cover)
    private val songDuration: TextView = itemView.findViewById(R.id.song_time)

    fun bind(item: Track) {
        Glide.with(itemView.context)
            .load(item.artworkUrl)
            .placeholder(R.drawable.placeholder)
            .into(songImage)

        songDuration.text = item.trackTime
        songTitle.text = item.trackName
        songSubtitle.text = item.artistName
    }
}