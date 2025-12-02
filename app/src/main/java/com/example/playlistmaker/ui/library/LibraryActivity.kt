package com.example.playlistmaker.ui.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity: AppCompatActivity() {

    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().add(R.id.fragment_library, LibraryFragment()).commit()
//        }

        binding.viewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 ->  tab.text = getString(R.string.fav_tracks_tab_title)
                1 -> tab.text = getString(R.string.playlists_tab_title)
            }
        }
        tabMediator.attach()

        binding.libraryToolbar.setNavigationOnClickListener {
            finish()
        }

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


    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}