package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import java.text.SimpleDateFormat
import java.util.Locale

class TrackHistoryAdapter(
    private var items: List<Track>,
    private val context: Context
) : RecyclerView.Adapter<TrackHistoryAdapter.TracksViewHolder> () {

    private val trackManager = TrackManager(context)
    private val sharedPreferences = context
        .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        items = trackManager.getTracksList(sharedPreferences)
        holder.bind(items[position])
        var itemsList = items as ArrayList<Track>

        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
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
                trackManager.saveTracksList(itemsList)
                trackManager.saveCurrentTrack(track)
                this.notifyDataSetChanged()


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

    class TracksViewHolder(
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

            songDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis.toLong())
            songTitle.text = item.trackName
            songSubtitle.text = item.artistName
        }
    }

}