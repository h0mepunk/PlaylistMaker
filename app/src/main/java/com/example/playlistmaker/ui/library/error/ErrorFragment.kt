package com.example.playlistmaker.ui.library.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.databinding.FragmentErrorBinding
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment

class ErrorFragment: Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ERROR_TEXT = "error_text"
        private const val BUTTON_VISIBLE = "button_visible"

        fun newInstance(
            errorText: String,
            buttonVisibility: Boolean
        ) = ErrorFragment().apply {
            arguments = Bundle().apply {
                putString(ERROR_TEXT, errorText)
                putBoolean(BUTTON_VISIBLE, buttonVisibility)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorText = requireArguments().getString(ERROR_TEXT)
        val buttonVisibility = requireArguments().getBoolean(BUTTON_VISIBLE)
        binding.placeholderMessageText.text = errorText
        binding.errorButton.visibility = if(buttonVisibility) View.VISIBLE else View.INVISIBLE
            binding.errorButton.setOnClickListener {
                //TODO implement
            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}