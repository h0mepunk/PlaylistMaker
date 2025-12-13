package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.presentation.main.MainState
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.ui.library.LibraryFragment
import com.example.playlistmaker.ui.search.SearchFragment
import com.example.playlistmaker.ui.settings.SettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainFragment: Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.mediaButton.setOnClickListener { viewModel.mediaButtonTap() }

        binding.searchButton.setOnClickListener { viewModel.searchButtonTap() }

        binding.settingsButton.setOnClickListener { viewModel.settingsButtonTap() }
    }

    fun mediaButtonTap() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, LibraryFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    fun searchButtonTap() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, SearchFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    fun settingsButtonTap() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, SettingsFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    fun render(mainState: MainState) {
        when (mainState) {
            is MainState.Settings -> {
                settingsButtonTap()
            }
            is MainState.Search -> {
                searchButtonTap()
            }
            is MainState.Track ->  {
                mediaButtonTap()
            }
        }
    }
}