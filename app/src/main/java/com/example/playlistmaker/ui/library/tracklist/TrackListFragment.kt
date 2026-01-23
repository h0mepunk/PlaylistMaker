package com.example.playlistmaker.ui.library.tracklist

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
import com.example.playlistmaker.databinding.FragmentLibraryContentBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.LibraryViewModel
import com.example.playlistmaker.presentation.library.TrackListState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class TrackListFragment : Fragment() {

    private lateinit var trackList: List<Track>
    companion object {
        private const val TRACK_LIST = "track_list"

        fun createArgs(trackList: List<Track>) = Bundle().apply {
                putString(TRACK_LIST, trackList.toString())
        }

        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
            arguments = createArgs(trackList)
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

    private lateinit var binding : FragmentLibraryContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryContentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryViewModel.observeTracksState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACK_LIST, trackList.toString())
    }

    fun getErrorFragment(
        errorText: String,
        buttonVisibility: Boolean
    ) = ErrorFragment().apply {
        arguments = PlaylistsFragment.Companion.setErrorArgs(errorText, buttonVisibility)
    }

    fun showError(
        errorText: String,
        buttonVisibility: Boolean
    ) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_library_content,
                getErrorFragment(errorText, buttonVisibility)
            )
        }
    }

    fun renderState(state: TrackListState) {
        when(state) {
            is TrackListState.TracksEmpty -> {
                trackList = emptyList()
                showError(
                    getString(R.string.placeholder_fav_message),
                    false
                )
            }
            is TrackListState.TracksContent -> {
                trackList = state.trackList
                findNavController().navigate(
                    R.id.fragment_library_content,
                    createArgs(state.trackList)
                )
            }
        }
    }
}