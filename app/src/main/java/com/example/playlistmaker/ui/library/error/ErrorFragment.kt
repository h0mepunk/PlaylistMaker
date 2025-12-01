package com.example.playlistmaker.ui.library.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.library.playlist.PlaylistsFragment

class ErrorFragment: Fragment() {
    companion object {
        private const val ERROR_TEXT = "error_text"
        private const val BUTTON_VISIBLE = "button_visible"

        fun newInstance(
            errorText: String,
            buttonVisibility: Boolean
        ) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(
                    ERROR_TEXT,
                    BUTTON_VISIBLE
                    )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorText = requireArguments().getInt("ERROR_TEXT")
        val buttonVisibility = requireArguments().getBoolean("BUTTON_VISIBLE")
    }
}