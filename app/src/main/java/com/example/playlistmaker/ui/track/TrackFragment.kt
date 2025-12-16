package com.example.playlistmaker.ui.track

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.presentation.track.TrackState
import com.example.playlistmaker.presentation.track.TrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TrackFragment : Fragment() {

    private val viewModel by viewModel<TrackViewModel>()

    private lateinit var binding: FragmentMediaBinding

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

        binding.mediaTrackTitle.text = viewModel.currentTrack.trackName
        binding.mediaTrackArtist.text = viewModel.currentTrack.artistName
        binding.mediaInfoAlbumValue .text = viewModel.currentTrack.collectionName
        binding.mediaInfoGenreValue.text = viewModel.currentTrack.primaryGenreName
        binding.mediaInfoYearValue.text = viewModel.currentTrack.releaseDate
        binding.mediaInfoLengthValue.text = viewModel.currentTrack.trackTime
        binding.mediaInfoCountryValue.text = viewModel.currentTrack.country

        binding.mediaButtonPlay.setOnClickListener {
            Log.e("TrackActivity","play/stop button clicked")
            viewModel.playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
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

    fun render(state: TrackState) {
        when (state) {
            is TrackState.Paused -> {
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_play)
            }
            is TrackState.Playing -> {
                binding.mediaButtonPlay.setBackgroundResource(R.drawable.media_stop)
                binding.mediaTrackTime.text = state.trackTime?: getString(R.string.start_time_zero)
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


}