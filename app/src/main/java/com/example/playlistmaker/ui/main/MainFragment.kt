package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.presentation.main.MainState
import com.example.playlistmaker.presentation.main.MainViewModel
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
        findNavController().navigate(R.id.library_fragment)
    }

    fun searchButtonTap() {
        findNavController().navigate(R.id.search_fragment)
    }

    fun settingsButtonTap() {
        findNavController().navigate(R.id.settings_fragment)
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