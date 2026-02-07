package com.example.playlistmaker.ui.library.playlist

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Const.EMPTY_STRING
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment: Fragment() {

    private var playlist: Playlist? = null

    var lastPlaylistName: String? = ""
    var lastPlaylistDescription: String?  = ""

    private var textWatcherName: TextWatcher? = null
    private var textWatcherDescription: TextWatcher? = null

    private val playlistCreateInteractor: PlaylistCreateInteractor by inject()

    companion object {
        private const val LOG_TAG = "PlaylistCreateFragment"

        private const val PLAYLIST = "playlist"

        fun newInstance(playlist: Playlist?) = PlaylistCreateFragment().apply {
            arguments = setPlaylistArg(playlist)
        }

        fun setPlaylistArg(playlist: Playlist?) = Bundle().apply {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistCreateViewModel.observePlaylistCreateState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        playlistCreateViewModel.getPlaylist()

        binding.editPlaylistName.setText(playlistCreateViewModel.onRestoreInstanceState(savedInstanceState).substringBefore(","))
        binding.editPlaylistDescription.setText(playlistCreateViewModel.onRestoreInstanceState(savedInstanceState).substringAfter(","))

        textWatcherName?.let { binding.editPlaylistName.addTextChangedListener(it) }
        textWatcherDescription?.let { binding.editPlaylistDescription.addTextChangedListener(it) }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.playlistCover.setImageURI(uri)
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.playlistImgPlaceholder.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
            val file = File(filePath, "first_cover.jpg")
            binding.playlistCover.setImageURI(file.toUri())
        }

        binding.cereatePlaylistButton.setOnClickListener {
            if (binding.editPlaylistName.text.isNotEmpty()) {
                playlistCreateViewModel.playlistName = binding.editPlaylistName.text.toString()
                playlistCreateViewModel.playlistDescription = binding.editPlaylistDescription.text.toString()
                playlistCreateViewModel.savePlaylist()
            }
        }

        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.editPlaylistName.setOnFocusChangeListener() { _, hasFocus -> }
        binding.editPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.editPlaylistName.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(binding.editPlaylistName.windowToken, 0)
                    playlistCreateViewModel.playlistName = binding.editPlaylistName.text.toString()
                }
            }
            false
        }

        binding.editPlaylistDescription.setOnFocusChangeListener() { _, hasFocus -> }
        binding.editPlaylistDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.editPlaylistDescription.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(binding.editPlaylistDescription.windowToken, 0)
                    playlistCreateViewModel.playlistDescription = binding.editPlaylistDescription.text.toString()
                }
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playlistCreateInteractor.saveCurrentPlaylist(playlist)
        playlistCreateViewModel.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcherName?.let { binding.editPlaylistName.removeTextChangedListener(it) }
        textWatcherDescription?.let { binding.editPlaylistDescription.removeTextChangedListener(it) }
    }

    override fun onResume() {
        super.onResume()
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
                showPlaylistData(playlist!!)
            }
        }
    }

    fun hidePlaylistData() {
    binding.editPlaylistDescription.setText(EMPTY_STRING)
        binding.editPlaylistName.setText(EMPTY_STRING)
        binding.editPlaylistDescriptionTitle.visibility = View.VISIBLE
        binding.editPlaylistNameTitle.visibility = View.VISIBLE
        binding.playlistImgPlaceholder.visibility = View.VISIBLE
        binding.playlistImgPlaceholder.setBackgroundResource(R.drawable.playlist_image_shape)
    }

    fun showPlaylistData(playlist: Playlist) {
        binding.editPlaylistDescription.setText(playlist.description)
        binding.editPlaylistName.setText(playlist.name)
        binding.editPlaylistDescriptionTitle.visibility = View.GONE
        binding.editPlaylistNameTitle.visibility = View.GONE
        showCover(playlist.imgUri)
    }

    fun showCover(path: String) {
        Glide.with(this)
            .load(File(path))
            .placeholder(R.drawable.playlist_image_shape)
            .apply(
                RequestOptions().transform(
                    RoundedCorners(
                        this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                    )
                )
            )
            .into(binding.playlistImgPlaceholder)
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}