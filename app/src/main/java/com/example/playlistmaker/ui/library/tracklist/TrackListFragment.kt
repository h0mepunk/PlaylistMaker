package com.example.playlistmaker.ui.library.tracklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.LibraryViewModel
import com.example.playlistmaker.ui.library.error.ErrorFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class TrackListFragment : Fragment() {

    private lateinit var trackList: LiveData<List<Track>>  //requireArguments().getString(TRACK_LIST)? emptyList()
    companion object {
        private const val TRACK_LIST = "track_list"

        fun newInstance(trackList: List<Track>) = TrackListFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_LIST, trackList.toString()) // Simplified
            }
        }
    }

    val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackList = libraryViewModel.getCurrentTrackList()
        libraryViewModel.getCurrentTrackList().observe(viewLifecycleOwner) {
            trackList ->
            if(trackList.isEmpty()) {
                showError(
                    getString(R.string.placeholder_fav_message),
                    false
                )
            } else {
                parentFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_library,
                        newInstance(trackList)
                    )
                    .commit()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACK_LIST, trackList.toString()) // add to json convertation
    }

    fun getErrorFragment(
        errorText: String ,
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
            replace(R.id.fragment_library, getErrorFragment(errorText, buttonVisibility))
            addToBackStack(null)
        }
    }
}