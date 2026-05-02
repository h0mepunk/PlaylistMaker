package com.example.playlistmaker.ui.library.tracklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryContentBinding
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.TrackListState
import com.example.playlistmaker.ui.library.error.ErrorFragment
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment
import com.example.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class TrackListFragment : Fragment() {

    private lateinit var trackList: List<Track>

    private val currentTrackInteractor: CurrentTrackInteractor by inject()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val TRACK_LIST = "track_list"
        private const val LOG_TAG = "TrackListFragment"

        fun createArgs(trackList: List<Track>) = Bundle().apply {
                putString(TRACK_LIST, trackList.toString())
        }

        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
            arguments = createArgs(trackList)
        }
    }

    val libraryViewModel by activityViewModel<TrackListViewModel>()

    private lateinit var binding : FragmentLibraryContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        binding = FragmentLibraryContentBinding.inflate(layoutInflater)
//        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryViewModel.observeTracksState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(R.id.action_library_fragment_to_track_fragment,
                Bundle().apply {
                    putString("track", track.toString())
                }
            )
        }
        adapter = TrackListAdapter(currentTrackInteractor) { track ->
            onTrackClickDebounce(track)
        }
        binding.trackLibraryListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.trackLibraryListRecycler.adapter = adapter

        libraryViewModel.getTrackList()
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.getTrackList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.trackLibraryListRecycler.adapter = null
    }

    fun getErrorFragment(
        errorText: String
    ) = ErrorFragment().apply {
        arguments = PlaylistsFragment.Companion.setErrorArgs(errorText)
    }

    fun showError(
        errorText: String
    ) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_library_content,
                getErrorFragment(errorText)
            )
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

    fun renderState(state: TrackListState) {
        when(state) {
            is TrackListState.TracksEmpty -> {
                Log.i(LOG_TAG,"TracksEmpty state")
                trackList = emptyList()
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
                showError(getString(R.string.placeholder_fav_message))
            }
            is TrackListState.TracksContent -> {
                Log.i(LOG_TAG,"trackList ${state.trackList}")
                trackList = state.trackList
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
                removeErrorFragment()
            }
        }
    }
}