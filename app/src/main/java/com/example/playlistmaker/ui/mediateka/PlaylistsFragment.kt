package com.example.playlistmaker.ui.mediateka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.domain.models.Track

class PlaylistsFragment : Fragment() {

    companion object {
        private const val PLAYLISTS_LIST = "playlists_list"

        fun newInstance(playlistsList: List<Track>) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLISTS_LIST, playlistsList.toString()) // Simplified
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistsList = requireArguments().getInt(NUMBER).toString()
    }
}