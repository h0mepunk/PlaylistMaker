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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Const.EMPTY_STRING
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.api.PlaylistCreateInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.PlaylistCreateState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment: Fragment() {

    private var playlist: Playlist? = null
    private var textWatcherName: TextWatcher? = null
    private var textWatcherDescription: TextWatcher? = null

    private val playlistCreateInteractor: PlaylistCreateInteractor by inject()

    private var fileName = ""

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

        val dialog =  MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { _, _ ->

            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }

        playlistCreateViewModel.getPlaylist()

        textWatcherName?.let { binding.editPlaylistName.addTextChangedListener(it) }
        textWatcherDescription?.let { binding.editPlaylistDescription.addTextChangedListener(it) }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    fileName = System.currentTimeMillis().toString() + ".jpg"
                    saveImageToPrivateStorage(uri, fileName)
                    val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
                    val file = File(filePath, fileName)
                    playlistCreateViewModel.coverUri = file.absolutePath
                    showCover(file.absolutePath)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    hideCover()
                }
            }

        binding.playlistCoverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.cereatePlaylistButton.setOnClickListener {
            Log.i(LOG_TAG, "Create button clicked, text: '${binding.editPlaylistName.text}'")
            if (binding.editPlaylistName.text.toString().trim().isNotEmpty()) {
                Log.i(LOG_TAG,"Playlist name is not empty, saving")
                playlistCreateViewModel.savePlaylist(
                    coverUri = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum/$fileName").toString(),
                    playlistName =  binding.editPlaylistName.text.toString(),
                    playlistDescription = binding.editPlaylistDescription.text.toString()
                )
                showToast("Плейлист ${binding.editPlaylistName.text} сохранён")
                Log.i(LOG_TAG, "Closing fragment after save")
                findNavController().popBackStack()
            } else {
                Log.i(LOG_TAG,"Playlist name is empty, not saving")
            }
        }

        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        //Если поле ввода названия плейлиста пустое, то пользователь видит текст-подсказку (hint).

        binding.editPlaylistName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.editPlaylistName.setText("")
                binding.editPlaylistName.hint = ""
            } else {
                if (binding.editPlaylistName.text.isEmpty()) {
                    binding.editPlaylistName.hint = getString(R.string.playlist_name_hint_text)
                }
            }
        }
        binding.editPlaylistDescription.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.editPlaylistDescription.setText("")
                binding.editPlaylistDescription.hint = ""
            } else {
                if (binding.editPlaylistDescription.text.isEmpty()) {
                    binding.editPlaylistDescription.hint = getString(R.string.playlist_description_hint_text)
                }
            }
        }
        binding.editPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.editPlaylistName.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(binding.editPlaylistName.windowToken, 0)
                    playlistCreateViewModel.playlistName = binding.editPlaylistName.text.toString()
                    Log.i(LOG_TAG,"name set to ${playlistCreateViewModel.playlistName} on done")
                }
            }
            false
        }

        binding.editPlaylistDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.editPlaylistDescription.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(binding.editPlaylistDescription.windowToken, 0)
                    playlistCreateViewModel.playlistDescription = binding.editPlaylistDescription.text.toString()
                    Log.i(LOG_TAG,"description set to ${playlistCreateViewModel.playlistDescription} on done")
                }
            }
            false
        }

        textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                playlistCreateViewModel.playlistName = s?.toString() ?: ""
                binding.cereatePlaylistButton.isEnabled = s?.isNotEmpty() == true
                Log.i(LOG_TAG,"name set to ${playlistCreateViewModel.playlistName} on text changed")
            }

            override fun afterTextChanged(s: Editable?) {
                playlistCreateViewModel.playlistName = s?.toString() ?: ""
                binding.cereatePlaylistButton.isEnabled = s?.isNotEmpty() == true
                Log.i(LOG_TAG,"name set to ${playlistCreateViewModel.playlistName} on after text changed")
                if (s != null) {
                    showName()
                } else {
                    hideName()
                }
            }
        }

        textWatcherDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                playlistCreateViewModel.playlistDescription = s?.toString() ?: ""
                Log.i(LOG_TAG,"description set to ${playlistCreateViewModel.playlistDescription} on text changed")
            }

            override fun afterTextChanged(s: Editable?) {
                playlistCreateViewModel.playlistDescription = s?.toString() ?: ""
                Log.i(LOG_TAG,"description set to ${playlistCreateViewModel.playlistDescription} on after text changed")
                if (s != null) {
                    showDescription()
                } else {
                    hideDescription()
                }
            }
        }

        binding.editPlaylistName.addTextChangedListener(textWatcherName)
        binding.editPlaylistDescription.addTextChangedListener(textWatcherDescription)

        binding.playlistToolbar.setNavigationOnClickListener {
            Log.i(LOG_TAG,"Checking unsaved data")
                if(
                    playlistCreateViewModel.playlistDescription.isNotEmpty() ||
                    playlistCreateViewModel.playlistName.isNotEmpty() ||
                    playlistCreateViewModel.coverUri.isNotEmpty()) {
                    Log.i(LOG_TAG,"Playlist has unsaved data, showing dialog")
                    dialog.show()
                } else {
                    Log.i(LOG_TAG,"No unsaved data, navigating back")
                    findNavController().popBackStack()
                }
            }

        playlistCreateViewModel.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playlistCreateViewModel.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcherName?.let { binding.editPlaylistName.removeTextChangedListener(it) }
        textWatcherDescription?.let { binding.editPlaylistDescription.removeTextChangedListener(it) }
    }

    override fun onResume() {
        super.onResume()
        if(playlistCreateViewModel.coverUri.isNotEmpty()) {
            showCover(playlistCreateViewModel.coverUri)
        }

        if(playlistCreateViewModel.playlistDescription.isNotEmpty()) {
            binding.editPlaylistDescription.setText(playlistCreateViewModel.playlistDescription)
            showDescription()
        }
        if(playlistCreateViewModel.playlistName.isNotEmpty()) {
            binding.editPlaylistName.setText(playlistCreateViewModel.playlistName)
            showName()
        }
    }

    fun renderState(state: PlaylistCreateState) {
        when (state) {
            is PlaylistCreateState.PlaylistEmpty -> {
                Log.i(LOG_TAG,"Playlist empty state")
                playlistCreateViewModel.coverUri?.let {
                    val file = File(it)
                    if (it.isNotEmpty() && file.exists() && file.length() > 0) {
                        showCover(it)
                        return
                    }
                }
                hidePlaylistData()
            }

            is PlaylistCreateState.PlaylistContent -> {
                Log.i(LOG_TAG,"Playlist is shown: ${state.playlist}")
                playlist = state.playlist
                if (playlist!!.imgUri != EMPTY_STRING) {
                    showCover(playlist!!.imgUri)
                } else {
                    hideCover()
                }
                if (playlist!!.name != EMPTY_STRING) {
                    showName()
                } else {
                    hideName()
                }
                if (playlist!!.description != EMPTY_STRING) {
                    showDescription()
                } else {
                    hideDescription()
                }
            }
        }
    }

    fun hidePlaylistData() {
        hideName()
        hideDescription()
        hideCover()
    }

    fun showDescription() {
        binding.editPlaylistDescriptionTitle.visibility = View.VISIBLE
        binding.editPlaylistDescription.hint = ""
    }

    fun hideDescription() {
        binding.editPlaylistDescriptionTitle.visibility = View.GONE
        binding.editPlaylistDescription.hint = getString(R.string.playlist_description_hint_text)
    }

    fun hideName() {
        binding.editPlaylistNameTitle.visibility = View.GONE
        binding.editPlaylistName.hint = getString(R.string.playlist_name_hint_text)
        binding.cereatePlaylistButton.isEnabled = false
    }

    fun showName() {
        binding.editPlaylistNameTitle.visibility = View.VISIBLE
        binding.cereatePlaylistButton.isEnabled = true
        binding.editPlaylistName.hint = ""
    }

    fun hideCover() {
        binding.playlistImgPlaceholder.visibility = View.VISIBLE
        binding.playlistCoverImage.visibility = View.GONE
        binding.playlistCoverBorder.visibility = View.VISIBLE
    }

    fun showCover(path: String) {
        val file = File(path)
        Log.i(LOG_TAG, "showCover path: $path, exists: ${file.exists()}, length: ${file.length()}")
        if (file.exists() && file.length() > 0) {
            binding.playlistImgPlaceholder.visibility = View.GONE
            binding.playlistCoverBorder.visibility = View.GONE
            binding.playlistCoverImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(file)
                .centerCrop()
                .apply(
                    RequestOptions().transform(
                        RoundedCorners(
                            this.resources.getDimension(R.dimen.media_cover_corner_radius).toInt()
                        )
                    )
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(binding.playlistCoverImage)
        } else {
            Log.e(LOG_TAG, "Cover file not found or empty, showing placeholder")
            hideCover()
        }
    }

    private fun cropCenterSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    private fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        if (bitmap != null) {
            val cropped = cropCenterSquare(bitmap)
            val scaled = Bitmap.createScaledBitmap(cropped, 500, 500, true)
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            Log.i(LOG_TAG, "Image saved: ${file.absolutePath}, exists: ${file.exists()}, length: ${file.length()}")
        } else {
            Log.e(LOG_TAG, "Bitmap decode failed for uri: $uri")
        }
    }

    private fun showToast(message: String?) {
        Log.i(LOG_TAG, "showToast: $message")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), message?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }
}