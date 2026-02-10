package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.track.TrackState
import com.example.playlistmaker.presentation.track.TrackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TrackFragment : Fragment() {

    private val viewModel by viewModel<TrackViewModel>()

    private lateinit var binding: FragmentMediaBinding

    private var adapter: PlaylistsAdapterMedia? = null

    private lateinit var playlistsList: List<Playlist>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.onCreate()
        binding = FragmentMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { liked ->
            setLikeButton(liked)
        }

        binding.mediaTrackTitle.text = viewModel.currentTrack.trackName
        binding.mediaTrackArtist.text = viewModel.currentTrack.artistName
        binding.mediaInfoAlbumValue .text = viewModel.currentTrack.collectionName
        binding.mediaInfoGenreValue.text = viewModel.currentTrack.primaryGenreName
        binding.mediaInfoYearValue.text = viewModel.currentTrack.releaseDate
        binding.mediaInfoLengthValue.text = viewModel.currentTrack.trackTime
        binding.mediaInfoCountryValue.text = viewModel.currentTrack.country
        binding.playlistBottomSheetListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.mediaButtonPlay.setOnClickListener {
            Log.i(LOG_TAG,"play/stop button clicked")
            viewModel.onPlayButtonClicked(viewModel.currentTrack.trackTime)
        }

        binding.mediaButtonLike.setOnClickListener {
            viewModel.onLikeButtonClicked()
        }

        binding.mediaToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.newPlaylistButtonBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().navigate(R.id.action_track_fragment_to_playlistCreateFragment)
        }

        adapter = PlaylistsAdapterMedia { playlist ->
            if (viewModel.addTrackToPlaylist(playlist)) {
                showToast("Добавлено в плейлист ${playlist.name}")
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                showToast("Трек уже добавлен в плейлист ${playlist.name}")
            }
        }
        binding.playlistBottomSheetListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistBottomSheetListRecycler.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                adapter!!.items = playlists
                adapter!!.notifyDataSetChanged()
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        viewModel.pausePlayer(viewModel.currentTrack.trackTime)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.pausePlayer(viewModel.currentTrack.trackTime)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        viewModel.onPlayButtonClicked(viewModel.currentTrack.trackTime)
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.mediaButtonAdd.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer(viewModel.currentTrack.trackTime)
    }


    fun showCover(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.media_cover_preview)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(binding.mediaTrackCover)
    }

    fun setLikeButton(liked: Boolean) {
        if (liked) {
            binding.mediaButtonLike.setBackgroundResource (R.drawable.media_liked)
            Log.i(LOG_TAG,"track is favorite")
        } else {
            binding.mediaButtonLike.setBackgroundResource(R.drawable.media_like)
            Log.i(LOG_TAG,"track is not favorite")
        }
    }

    fun render(state: TrackState) {
        when (state) {
            is TrackState.Paused -> {
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_play)
            }
            is TrackState.Playing -> {
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_stop)
                binding.mediaTrackTime.text = state.trackTime?: getString(R.string.start_time_zero)
                Log.i(LOG_TAG, "time = " + state.trackTime.toString())
            }
            is TrackState.Stopped -> {
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_play)
                binding.mediaTrackTime.text = getString(R.string.start_time_zero)
            }
            is TrackState.Init -> {
                showCover(state.previewImgUrl)
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_play)
                binding.mediaTrackTime.text = getString(R.string.start_time_zero)
            }
            is TrackState.Prepared -> { binding.mediaButtonPlay.isEnabled = true }
        }
    }

    fun showToast(additionalMessage: String?) {
        Log.i(LOG_TAG, "showToast: $additionalMessage")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), additionalMessage?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        private const val LOG_TAG = "TrackFragment"
    }
}