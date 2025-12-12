package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.LibraryViewModel
import com.example.playlistmaker.ui.library.error.ErrorFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsList: LiveData<List<Playlist>>

    companion object {
        private const val PLAYLISTS_LIST = "playlists_list"

        fun newInstance(playlistsList: List<Playlist>) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLISTS_LIST, playlistsList.toString()) // Simplified
            }
        }
    }

    val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsList = libraryViewModel.getCurrentPlaylist()
        libraryViewModel.getCurrentPlaylist().observe(viewLifecycleOwner) {
            playlistsList ->
                if (playlistsList.isEmpty()) {
                    showError(
                        getString(R.string.placeholder_playlists_message),
                        buttonVisibility = true
                    )
                } else {
                    parentFragmentManager.beginTransaction()
                        .add(
                            R.id.fragment_playlists,
                            newInstance(playlistsList)
                        )
                        .commit()
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
        arguments = bundleOf(
            "error_text" to errorText,
            "button_visible" to buttonVisibility
        )
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