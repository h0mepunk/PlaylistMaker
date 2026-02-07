package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import com.example.playlistmaker.ui.library.tracklist.TrackListAdapter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsList: List<Playlist>

    private var adapter: PlaylistsAdapter? = null

    companion object {
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

        adapter = PlaylistsAdapter()
        binding.playlistLibraryListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistLibraryListRecycler.adapter = adapter

        playlistViewModel.getPlaylists()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLISTS_LIST, playlistsList.toString()) // add to json convertation
    }

    fun getErrorFragment(
        errorText: String
    ) = ErrorFragment().apply {
        arguments = setErrorArgs(errorText)
    }

    fun showError(
        errorText: String,
    ) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_playlists,
                getErrorFragment(errorText)
            )
        }
    }

    fun renderState(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.PlaylistsEmpty -> {
                Log.i(LOG_TAG,"Playlists empty state")
                playlistsList = emptyList()
                showError(getString(R.string.placeholder_playlists_message))
            }
            is PlaylistsState.PlaylistsContent -> {
                Log.i(LOG_TAG,"playlists : ${state.playlistList}")
                removeErrorFragment()
                playlistsList = state.playlistList

            }
        }
    }

    private fun removeErrorFragment() {
        val errorFragment = childFragmentManager.findFragmentById(R.id.fragment_library_content)
        if (errorFragment is ErrorFragment) {
            childFragmentManager.commit {
                remove(errorFragment)
            }
        }
    }
}