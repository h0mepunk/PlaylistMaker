package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment: Fragment() {

//    companion object {
//        private const val TRACK_LIST = "track_list"
//
//        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
//            arguments = Bundle().apply {
//                putString(TRACK_LIST, trackList.toString()) // Simplified
//            }
//        }
//    }

    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentLibraryBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = LibraryViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 ->  tab.text = getString(R.string.fav_tracks_tab_title)
                1 -> tab.text = getString(R.string.playlists_tab_title)
            }
        }

        tabMediator.attach()

        binding.libraryToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.main_fragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    fun setCurrentPlaylist(playlistList: List<Playlist>) {
        //TODO
    }

    fun getCurrentPlaylist(): List<Playlist> {
        //TODO
        return emptyList()
    }

    fun setCurrentTrackList(trackList: List<Track>) {
        //TODO
    }

    fun getCurrentTrackList(): List<Track> {
        //TODO
        return emptyList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}