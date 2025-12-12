package com.example.playlistmaker.ui.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment
import com.example.playlistmaker.ui.library.tracklist.TrackListFragment

class LibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    val trackList: List<Track> = emptyList() // add test data
    val playlistsList: List<Playlist> = emptyList() // add test data

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TrackListFragment.newInstance(trackList)
            1 -> PlaylistsFragment.newInstance(playlistsList)
            else -> TrackListFragment.newInstance(trackList)
        }
    }
}