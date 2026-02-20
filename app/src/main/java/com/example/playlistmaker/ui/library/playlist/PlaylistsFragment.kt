package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import com.example.playlistmaker.ui.library.tracklist.TrackListAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsList: List<Playlist>

    private var adapter: PlaylistsAdapter? = null

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    companion object {

        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val PLAYLISTS_LIST = "playlists_list"

        private const val LOG_TAG = "PlaylistFragment"

        fun newInstance(playlistsList: List<Playlist>) = PlaylistsFragment().apply {
            arguments = setPlaylistArg(playlistsList)
        }

        fun setPlaylistArg(playlistsList: List<Playlist>) = Bundle().apply {
                putString(PLAYLISTS_LIST, playlistsList.toString()) // Simplified
            }

        fun setErrorArgs(
            errorText: String
        ) = Bundle().apply {
            putString("error_text", errorText)
        }
    }

    val playlistViewModel by activityViewModel<PlaylistViewModel>()

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistViewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            findNavController().navigate(R.id.action_playlistsFragment_to_playlistPageFragment,
                Bundle().apply {
                    putString("playlist", playlist.toString())
                }
            )
        }
//        adapter = TrackListAdapter(currentTrackInteractor) { track ->
//            onTrackClickDebounce(track)
//        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_library_fragment_to_playlistCreateFragment
            )
        }
        adapter = PlaylistsAdapter { playlist ->
            onPlaylistClickDebounce(playlist)
        }
        binding.playlistListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistListRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistListRecycler.adapter = adapter

        playlistViewModel.getPlaylists()
    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.getPlaylists()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLISTS_LIST, playlistsList.toString()) // add to json convertation
    }

    fun renderState(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.PlaylistsEmpty -> {
                Log.i(LOG_TAG,"Playlists empty state")
                playlistsList = emptyList()
                adapter?.items = playlistsList
                adapter?.notifyDataSetChanged()
                binding.playlistListRecycler.visibility = View.GONE
                binding.placeholderView.visibility = View.VISIBLE
            }
            is PlaylistsState.PlaylistsContent -> {
                Log.i(LOG_TAG,"playlists : ${state.playlistList}")
                binding.placeholderView.visibility = View.GONE
                binding.playlistListRecycler.visibility = View.VISIBLE
                playlistsList = state.playlistList
                adapter?.items = playlistsList
                adapter?.notifyDataSetChanged()
            }
        }
    }
}