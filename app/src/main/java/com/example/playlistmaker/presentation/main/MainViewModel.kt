package com.example.playlistmaker.presentation.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.TrackActivity

class MainViewModel: ViewModel() {

    fun mediaButtonTap(context: Context) {
        val trackActivity = Intent(context, TrackActivity::class.java)
        context.startActivity(trackActivity)
    }

    fun searchButtonTap(context: Context) {
        val searchActivity = Intent(context, SearchActivity::class.java)
        context.startActivity(searchActivity)
    }

    fun settingsButtonTap(context: Context) {
        val settingsActivity = Intent(context, SettingsActivity::class.java)
        context.startActivity(settingsActivity)
    }
}