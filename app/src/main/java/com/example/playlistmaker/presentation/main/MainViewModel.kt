package com.example.playlistmaker.presentation.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.ui.main.App
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.TrackActivity

class MainViewModel( private val context: Context): ViewModel() {

    companion object {

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                MainViewModel(app)
            }
        }
    }
    fun mediaButtonTap() {
        val trackActivity = Intent(context, TrackActivity::class.java)
        context.startActivity(trackActivity)
    }

    fun searchButtonTap() {
        val searchActivity = Intent(context, SearchActivity::class.java)
        context.startActivity(searchActivity)
    }

    fun settingsButtonTap() {
        val settingsActivity = Intent(context, SettingsActivity::class.java)
        context.startActivity(settingsActivity)
    }
}