package com.example.playlistmaker.ui.library.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlaylistCreateFragment: Fragment() {

    private lateinit var playlist: Playlist

    companion object {
        private const val LOG_TAG = "PlaylistCreateFragment"

        private const val PLAYLIST = "playlist"

        fun newInstance(playlist: Playlist) = PlaylistCreateFragment().apply {
            arguments = setPlaylistArg(playlist)
        }

        fun setPlaylistArg(playlist: Playlist) = Bundle().apply {
            putString(PLAYLIST, playlist.toString())
        }
    }

    val playlistCreateViewModel by activityViewModel<PlaylistCreateViewModel>()

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        return binding.root
    }

    override onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistCreateViewModel.observePlaylistCreateState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        plalistCreateViewModel.getPlaylist()
    }

    fun renderState(state: PlaylistCreateState) {
        when (state) {
            is PlaylistCreateState.PlaylistEmpty -> {
                Log.i(LOG_TAG,"Playlist empty state")
                hidePlaylistData()
            }

            is PlaylistCreateState.PlaylistContent -> {
                Log.i(LOG_TAG,"Playlist is shown: ${state.playlist}")
                playlist = state.playlist

                // Показать экран с данными плейлиста
            }
        }
    }

    fun hidePlaylistData() {
        // Скрыть данные плейлиста и показать экран с сообщением об отсутствии данных
    }

    fun showPlaylistData(playlist: Playlist) {
        // Показать данные плейлиста на экране
    }
}