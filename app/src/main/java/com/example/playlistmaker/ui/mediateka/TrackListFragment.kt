package com.example.playlistmaker.ui.mediateka

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.playlistmaker.domain.models.Track

class TrackListFragment : Fragment() {
    companion object {
        private const val TRACK_LIST = "track_list"

        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_LIST, trackList.toString()) // Simplified
            }
        }
    }
}