package com.example.playlistmaker.ui.library.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentErrorBinding

class ErrorFragment: Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    private var errorText: String? = null

    companion object {
        private const val ERROR_TEXT = "error_text"

        fun newInstance(
            errorText: String,
        ) = ErrorFragment().apply {
            arguments = Bundle().apply {
                putString(ERROR_TEXT, errorText)
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
        errorText = requireArguments().getString(ERROR_TEXT)
        binding.placeholderMessageText.text = errorText

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ERROR_TEXT, errorText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}