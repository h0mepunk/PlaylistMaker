package com.example.playlistmaker.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistPageBinding
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.PlaylistPageState
import com.example.playlistmaker.ui.track.TrackFragment
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistPageFragment: Fragment() {

    private lateinit var trackList: List<Track>

    private var adapter: TracksPlaylistPageAdapter? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var onTrackClickDebounceDelete: (Track) -> Unit

    companion object {

        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val TRACKS_LIST = "tracks_list_playlist_page"

        private const val LOG_TAG = "PlaylistPageFragment"

        fun newInstance(trackList: List<Track>) = PlaylistPageFragment().apply {
            arguments = setTracksArg(trackList)
        }

        fun setTracksArg(trackList: List<Track>) = Bundle().apply {
            putString(TRACKS_LIST, trackList.toString()) // Simplified
        }
    }
    val viewModel by activityViewModel<PlaylistPageViewModel>()
    private val currentTrackInteractor: CurrentTrackInteractor by inject()

    private lateinit var binding: FragmentPlaylistPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onCreate()
        viewModel.observeTracksState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        binding.playlistPageTitle.text = viewModel.currentPlaylist.name
        binding.playlistPageYear.text = viewModel.currentPlaylist.description
        binding.playlistPageTimeCount.text = buildString {
            append(formatterTimeMinutes(viewModel.currentPlaylist.timeTotal))
            append(" минут")
        }
        binding.playlistPageTracksCount.text = buildString {
            append(viewModel.currentPlaylist.tracksCount)
            append(" треков")
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistPageFragment_to_track_fragment,
                Bundle().apply {
                    putString("tracks", track.toString())
                }
            )
        }

        fun dialog(track: Track) =  MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить трек?")
            .setNegativeButton("НЕТ"
            ) { _, _ -> }
            .setPositiveButton("ДА") { _, _ ->
                if (viewModel.currentPlaylist.tracksCount == 1) {
                    showToast("Нельзя удалить последний трек из плейлиста")
                }
                else {
                    viewModel.deleteTrackFromPlaylist(track)
                    viewModel.getCurrentPlaylist()
                    viewModel.getTracks()
                    showTracksCount(viewModel.currentPlaylist.tracksCount)
                    showTimeTotal(viewModel.currentPlaylist.timeTotal)
                }
            }

        val dialogShare =  MaterialAlertDialogBuilder(requireContext())
            .setTitle("В этом плейлисте нет списка треков, которым можно поделиться")
            .setPositiveButton("Ок") { _, _ ->
            }

        onTrackClickDebounceDelete = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            dialog(track).show()
        }

        adapter = TracksPlaylistPageAdapter(
            currentTrackInteractor,
             onTrackClick = { track ->
                onTrackClickDebounce(track)
            },
            onTrackLongClick = { track ->
                onTrackClickDebounceDelete(track)
            }
        )

        val bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.playlistBottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehaviorMenu.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.playlistBottomsheetMenuShare.setOnClickListener {
            share(dialogShare)
        }

        binding.playlistBottomsheetMenuDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Хотите удалить плейлист?")
                .setMessage("При удалении плейлиста удалится вся информация о добавленных в него треках")
                .setNegativeButton("НЕТ"
                ) { _, _ -> }
                .setPositiveButton("ДА") { _, _ ->
                    viewModel.deletePlaylist()
                    findNavController().popBackStack()
                }
                .show()
        }


        binding.playlistPageButtonMore.setOnClickListener {
            showCoverBottomsheet(viewModel.currentPlaylist.imgUri)
            binding.playlistNameBottomsheetMenu.text = viewModel.currentPlaylist.name
            binding.playlistTracksCountBottomsheetMenu.text = buildString {
                append(viewModel.currentPlaylist.tracksCount)
                append(" треков")
            }
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistPageButtonShare.setOnClickListener {
            share(dialogShare)
        }

        binding.playlistBottomSheetListRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistBottomSheetListRecycler.adapter = adapter

        binding.playlistPageToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        showTracks(viewModel.trackList)
    }

    fun showCover(path: String) {
        val file = File(path)
        Log.i(LOG_TAG, "showCover path: $path, exists: ${file.exists()}, length: ${file.length()}")
        if (file.exists() && file.length() > 0) {
            Glide.with(this)
                .load(path)
                .placeholder(R.drawable.playlist_page_image_placeholder)
                .into(binding.playlistPageCover)
        }
    }

    fun showCoverBottomsheet(path: String) {
        val file = File(path)
        Log.i(LOG_TAG, "showCover path: $path, exists: ${file.exists()}, length: ${file.length()}")
        if (file.exists() && file.length() > 0) {
            Glide.with(this)
                .load(path)
                .placeholder(R.drawable.placeholder)
                .into(binding.playlistCoverBottomsheetMenu)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrentPlaylist()
        showTracks(viewModel.trackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACKS_LIST, trackList.toString())
    }

    fun renderState(state: PlaylistPageState) {
        when(state) {
            is PlaylistPageState.Empty -> {
                Log.i(LOG_TAG,"Tracks empty state")
                trackList = emptyList()
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
            }
            is PlaylistPageState.Tracks -> {
                Log.i(LOG_TAG,"tracks : ${state.tracks}")
                binding.playlistBottomSheetListRecycler.visibility = View.VISIBLE
                trackList = state.tracks
                adapter?.items = trackList
                adapter?.notifyDataSetChanged()
                showCover(state.playlist.imgUri)
            }
        }
    }

    fun showTracksCount(count: Int) {
        binding.playlistPageTracksCount.text = buildString {
            append(count)
            append(" треков")
        }
    }

    fun showTimeTotal(time: Long) {
        binding.playlistPageTimeCount.text = buildString {
            append(formatterTimeMinutes(time))
            append(" минут")
        }
    }

    fun showTracks(tracks: List<Track>) {
        viewModel.getTracks()
        trackList = tracks
        adapter?.items = trackList
        adapter?.notifyDataSetChanged()
    }

    private fun formatterTimeMinutes(time: Long): String {
        val minutes = time / 60000
        return String.format("%d", minutes)
    }

    private fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String {
        val header = "${playlist.name}\n${playlist.description}\n${playlist.tracksCount} треков\n"
        val trackList = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
        }.joinToString("\n")
        return header + trackList
    }

    fun showToast(message: String?) {
        Log.i(LOG_TAG, "showToast: $message")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), message?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun share(dialogShare: MaterialAlertDialogBuilder) {
        if (viewModel.currentPlaylist.tracksCount == 0) {
            dialogShare.show()
        } else {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getPlaylistShareText(viewModel.currentPlaylist,viewModel.trackList)
            )
            startActivity(intent)
        }
    }
}