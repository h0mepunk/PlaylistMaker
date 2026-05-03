package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.presentation.library.TrackListState
import com.example.playlistmaker.ui.common.PlaylistMakerTheme
import com.example.playlistmaker.ui.library.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.library.tracklist.TrackListViewModel
import com.example.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class LibraryFragment: Fragment() {

    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PlaylistMakerTheme(
                darkTheme = themeInteractor.getTheme()
            ) {
                LibraryScreen(
                    onTrackClick = { onTrackClickDebounce(it) },
                    onPlaylistClick = { playlist ->
                        playlistCreateInteractor.saveCurrentPlaylist(playlist)
                        onPlaylistClickDebounce(playlist)
                    },
                    onCreatePlaylistClick = {
                        findNavController().navigate(R.id.action_library_fragment_to_playlistCreateFragment)
                    },
                    tracksViewModel,
                    playlistViewModel
                )
            }
        }
    }


    private lateinit var onTrackClickDebounce: (Track) -> Unit
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val LOG_TAG = "TrackListFragment"

    }

    val tracksViewModel by activityViewModel<TrackListViewModel>()

    val playlistViewModel by activityViewModel<PlaylistViewModel>()

    override fun onResume() {
        super.onResume()
        tracksViewModel.getTrackList()
        playlistViewModel.getPlaylists()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tracksViewModel.observeTracksState().observe(viewLifecycleOwner) { state ->
            renderTracksState(state)
        }

        playlistViewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            renderPlaylistsState(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            tracksViewModel.saveCurrentTrack(track)
            findNavController().navigate(R.id.action_library_fragment_to_track_fragment,
                Bundle().apply {
                    putString("track", track.toString())
                }
            )
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            findNavController().navigate(R.id.action_library_fragment_to_playlistPageFragment,
                Bundle().apply {
                    putString("playlist_current", playlist.toString())
                }
            )
        }

        tracksViewModel.getTrackList()
        playlistViewModel.getPlaylists()
    }

    private lateinit var playlistsList: List<Playlist>


    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit


    private val playlistCreateInteractor: PlaylistCreateInteractor by inject()


    fun renderTracksState(state: TrackListState) {
        when(state) {
            is TrackListState.TracksEmpty -> {
                Log.i(LOG_TAG,"TracksEmpty state")
                tracksViewModel.setTrackList(emptyList())
                tracksViewModel.setErrorVisibility(true)
            }
            is TrackListState.TracksContent -> {
                Log.i(LOG_TAG,"trackList ${state.trackList}")
                tracksViewModel.setTrackList(state.trackList)
                tracksViewModel.setErrorVisibility(false)
            }
        }
    }

    fun renderPlaylistsState(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.PlaylistsEmpty -> {
                Log.i(LOG_TAG,"Playlists empty state")
                playlistsList = emptyList()
                playlistViewModel.setPlaylistsList(playlistsList)
                playlistViewModel.setErrorVisibility(true)
            }
            is PlaylistsState.PlaylistsContent -> {
                Log.i(LOG_TAG,"playlists : ${state.playlistList}")
                playlistsList = state.playlistList
                playlistViewModel.setPlaylistsList(playlistsList)
                playlistViewModel.setErrorVisibility(false)
            }
        }
    }
}