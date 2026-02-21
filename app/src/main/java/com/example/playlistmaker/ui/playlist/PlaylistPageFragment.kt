package com.example.playlistmaker.ui.playlist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistPageBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.presentation.playlist.PlaylistPageState
import com.example.playlistmaker.ui.library.playlist.PlaylistsAdapter
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistPageFragment: Fragment() {

    private lateinit var trackList: List<Track>

    private var adapter: TracksPlaylistPageAdapter? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {

        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val TRACKS_LIST = "tracks_list_playlist_page"

        private const val LOG_TAG = "PlaylistPageFragment"

        fun newInstance(trackList: List<Track>) = PlaylistPageFragment().apply {
            arguments = setTracksArg(trackList)
        }

        fun setTracksArg(trackList: List<Track>) = Bundle().apply {
            putString(TRACKS_LIST, trackList.toString()) // Simplified
        }
    }
    val viewModel by activityViewModel<PlaylistPageViewModel>()

    private lateinit var binding: FragmentPlaylistPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onCreate()
        viewModel.observeTracksState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        binding.playlistPageTitle.text = viewModel.currentPlaylist.name
        binding.playlistPageYear.text = viewModel.currentPlaylist.description
        binding.playlistPageTimeCount.text = formatterTime(viewModel.currentPlaylist.timeTotal)
        binding.playlistPageTracksCount.text = viewModel.currentPlaylist.tracks.length.toString()

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistPageFragment_to_track_fragment,
                Bundle().apply {
                    putString("tracks", track.toString())
                }
            )
        }

        adapter = TracksPlaylistPageAdapter { track ->
            onTrackClickDebounce(track)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }


        binding.playlistPageButtonMore.setOnClickListener {
            TODO()
        }
        binding.playlistPageButtonShare.setOnClickListener {
            TODO()
        }

        binding.playlistBottomSheetListRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistBottomSheetListRecycler.adapter = adapter

        binding.playlistPageToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getTracks()
        showTracks(viewModel.trackList)
    }

    fun showCover(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.playlist_page_image_placeholder)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(binding.playlistPageCover)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTracks()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACKS_LIST, trackList.toString())
    }

    fun renderState(state: PlaylistPageState) {
        when(state) {
            is PlaylistPageState.Empty -> {
                Log.i(LOG_TAG,"Tracks empty state")
                trackList = emptyList()
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
            }
            is PlaylistPageState.Tracks -> {
                Log.i(LOG_TAG,"tracks : ${state.tracks}")
                binding.playlistBottomSheetListRecycler.visibility = View.VISIBLE
                trackList = state.tracks
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
                showCover(state.playlist.imgUri.toUri())
            }
        }
    }

    fun showTracks(tracks: List<Track>) {
        trackList = tracks
        adapter?.items = trackList
        adapter?.notifyDataSetChanged()
    }

    private fun formatYear(timestamp: Long): String {
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun formatterTime(time: Long): String {
        val minutes = time / 60000
        val seconds = (time % 60000) / 1000
        return String.format("%d:%02d", minutes, seconds)
    }

}