package com.example.playlistmaker.ui.track

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Const.SONG_ARTIST_KEY
import com.example.playlistmaker.Const.SONG_NAME_KEY
import com.example.playlistmaker.Const.SONG_URL_KEY
import com.example.playlistmaker.R
import com.example.playlistmaker.services.track.MusicService
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.track.TrackState
import com.example.playlistmaker.presentation.track.TrackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TrackFragment() : Fragment() {

    private val viewModel by viewModel<TrackViewModel>()

    private lateinit var binding: FragmentMediaBinding

    private lateinit var intent: Intent

    private var adapter: PlaylistsAdapterMedia? = null

    private lateinit var playlistsList: List<Playlist>

    private var musicService: MusicService? = null

    private var lastClickedPlaylistName: String? = null

    private var isServiceBound = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.play()
        } else {
            showToast("Permission on foreground service denied")
        }
    }

    private var playerState: TrackState = TrackState.Init("")

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            musicService?.setPlayerStateListener(object : MusicService.PlayerStateListener {
                override fun onStateChanged(state: TrackState) {
                    playerState = state
                    render(state)
                }
            })
            viewModel.setMusicService(musicService!!)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

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

        viewModel.isFavorite.observe(viewLifecycleOwner) { liked ->
            setLikeButton(liked)
        }

        bindMusicService()

        showCover(artworkUrlForDisplay(viewModel.currentTrack.artworkUrl100))

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
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
            requireActivity().runOnUiThread {
                when(playerState) {
                    is TrackState.Playing -> {
                        viewModel.pause()
                    }
                    is TrackState.Prepared, is TrackState.Paused -> {
                        checkAndRequestPermission()
                        //viewModel.startForegroundMusicService(intent)
                    }
                    else -> { }
                }
            }
        }

        binding.mediaButtonLike.setOnClickListener {
            viewModel.onLikeButtonClicked()
        }

        binding.mediaToolbar.setNavigationOnClickListener {
            unbindMusicService()
            findNavController().popBackStack()
        }

        binding.newPlaylistButtonBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().navigate(R.id.action_track_fragment_to_playlistCreateFragment)
        }

        viewModel.trackAddedToPlaylist.observe(viewLifecycleOwner) { isAdded ->
            lastClickedPlaylistName?.let { playlistName ->
                playlistAddedToast(isAdded, playlistName)
                lastClickedPlaylistName = null
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        adapter = PlaylistsAdapterMedia { playlist ->
            lastClickedPlaylistName = playlist.name
            viewModel.addTrackToPlaylist(playlist) // вызовите ваш метод добавления трека
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
                        viewModel.pause()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.play()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
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

    override fun onPause() {
        super.onPause()
        if (playerState is TrackState.Playing && isServiceBound) {
            viewModel.startForegroundMusicService(intent)
            viewModel.showNotification()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
        viewModel.stopForegroundMusicService()
        render(playerState)
    }

    override fun onDestroyView() {
        requireActivity().runOnUiThread {
            when(playerState) {
                is TrackState.Playing -> {
                    viewModel.startForegroundMusicService(intent)
                    viewModel.showNotification()
                }
                else -> {
                    unbindMusicService()
                    isMusicServiceStarted = false
                }
            }
        }
        super.onDestroyView()
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
            }
            is TrackState.Playing -> {
                binding.mediaTrackTime.text = state.trackTime?: getString(R.string.start_time_zero)
                Log.i(LOG_TAG, "time = " + state.trackTime.toString())
            }
            is TrackState.Stopped -> {
                binding.mediaButtonPlay.switchState()
                binding.mediaTrackTime.text = TrackState.Stopped.timerText
            }
            is TrackState.Init -> {
                showCover(artworkUrlForDisplay(viewModel.currentTrack.artworkUrl100))
                binding.mediaTrackTime.text = getString(R.string.start_time_zero)
            }
            is TrackState.Prepared -> {
                binding.mediaButtonPlay.isEnabled = true
            }
        }
        binding.mediaButtonPlay.apply {
            setBackgroundResource(state.buttonImage)
            isEnabled = state.isPlayButtonEnabled
        }
    }

    private fun startMusicService(foreground: Boolean) {
        if (foreground) {
            requireActivity().startForegroundService(intent)
        } else {
            requireActivity().startService(intent)
        }
    }

    fun showToast(message: String?) {
        Log.i(LOG_TAG, "showToast: $message")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), message?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun playlistAddedToast(isAdded: Boolean, playlistName: String) {
        if (isAdded) {
            showToast("Добавлено в плейлист $playlistName")
        } else {
            showToast("Трек уже добавлен в плейлист $playlistName")
        }
    }


    private fun bindMusicService() {
        intent = Intent(requireActivity(), MusicService::class.java).apply {
            putExtra(SONG_URL_KEY, viewModel.currentTrack.previewUrl)
            putExtra(SONG_NAME_KEY, viewModel.currentTrack.trackName)
            putExtra(SONG_ARTIST_KEY, viewModel.currentTrack.artistName)
        }
        musicService?.startForeground(intent)
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        isServiceBound = true
    }

    private fun unbindMusicService() {
        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun artworkUrlForDisplay(url: String): String =
        url.replace("100x100bb", "600x600bb", ignoreCase = false)
            .replace("100x100", "600x600")

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (requireContext().checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE)
            } else {
                viewModel.play()
            }
        } else {
            viewModel.play()
        }
    }

    companion object {
        var isMusicServiceStarted = false
        private const val LOG_TAG = "TrackFragment"
    }
}