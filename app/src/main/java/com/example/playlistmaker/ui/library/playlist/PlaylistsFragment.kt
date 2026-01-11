package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.LibraryViewModel
import com.example.playlistmaker.ui.library.error.ErrorFragment
import com.example.playlistmaker.ui.library.tracklist.TrackListFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsList: LiveData<List<Playlist>>

    companion object {
        private const val PLAYLISTS_LIST = "playlists_list"

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

    val libraryViewModel by activityViewModel<LibraryViewModel>()

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsList = libraryViewModel.getCurrentPlaylist()
        libraryViewModel.getCurrentPlaylist().observe(viewLifecycleOwner) {
            playlistsList ->
                if (playlistsList.isEmpty()) {
                    showError(
                        getString(R.string.placeholder_playlists_message),
                        true
                    )
                } else {
                    findNavController().navigate(
                        R.id.fragment_playlists,
                        setPlaylistArg(playlistsList)
                    )
                }
        }
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
        parentFragmentManager.commit {
            replace(
                R.id.fragment_playlists,
                getErrorFragment(errorText, buttonVisibility)
            )
            addToBackStack(null)
        }
    }
}