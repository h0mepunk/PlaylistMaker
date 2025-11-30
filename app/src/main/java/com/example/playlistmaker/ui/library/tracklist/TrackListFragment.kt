package com.example.playlistmaker.ui.library.tracklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.library.error.ErrorFragment

class TrackListFragment : Fragment() {
    companion object {
        private const val TRACK_LIST = "track_list"

        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_LIST, trackList.toString()) // Simplified
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_library, ErrorFragment())
            .commit()
    }
}