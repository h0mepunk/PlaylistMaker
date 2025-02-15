package com.example.playlistmaker

import android.service.autofill.FillEventHistory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(
    itemView: View)
: RecyclerView.ViewHolder(itemView) {

    private val songTitle: TextView = itemView.findViewById(R.id.song_name)
    private val songSubtitle: TextView = itemView.findViewById(R.id.song_author)
    private val songImage: ImageView = itemView.findViewById(R.id.song_album_cover)
    private val songDuration: TextView = itemView.findViewById(R.id.song_time)

    fun bind(item: Track) {
        Log.e("???", "onBindViewHolder ${item}")
        Glide.with(itemView.context)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(songImage)

        songDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis.toLong())
        songTitle.text = item.trackName
        songSubtitle.text = item.artistName
    }
}