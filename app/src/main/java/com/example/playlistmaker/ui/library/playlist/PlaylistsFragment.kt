package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsList: List<Playlist>

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
            errorText: String,
            buttonVisibility: Boolean
        ) = Bundle().apply {
            putString("error_text", errorText)
            putBoolean("button_visible", buttonVisibility)
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

        playlistViewModel.getPlaylists()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLISTS_LIST, playlistsList.toString()) // add to json convertation
    }

    fun getErrorFragment(
        errorText: String,
        buttonVisibility: Boolean
    ) = ErrorFragment().apply {
        arguments = setErrorArgs(errorText, buttonVisibility)
    }

    fun showError(
        errorText: String,
        buttonVisibility: Boolean
    ) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_playlists,
                getErrorFragment(errorText, buttonVisibility)
            )
        }
    }

    fun renderState(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.PlaylistsEmpty -> {
                Log.i(LOG_TAG,"Playlists empty state")
                playlistsList = emptyList()
                showError(
                    getString(R.string.placeholder_playlists_message),
                    true
                )
            }
            is PlaylistsState.PlaylistsContent -> {
                Log.i(LOG_TAG,"playlists : ${state.playlistList}")
                playlistsList = state.playlistList
                    findNavController().navigate(
                        R.id.fragment_playlists,
                        setPlaylistArg(playlistsList)
                    )
            }
        }
    }
}