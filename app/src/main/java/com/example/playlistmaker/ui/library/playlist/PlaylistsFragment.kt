package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.library.error.ErrorFragment

class PlaylistsFragment : Fragment() {

    val playlistsList: List<Playlist> =  emptyList()//requireArguments().getString(PLAYLISTS_LIST)?: emptyList()

    companion object {
        private const val PLAYLISTS_LIST = "playlists_list"

        fun newInstance(playlistsList: List<Playlist>) = PlaylistsFragment().apply {
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
                R.id.fragment_library,
                getErrorFragment(errorText, buttonVisibility)
            )
            addToBackStack(null)
        }
    }
}