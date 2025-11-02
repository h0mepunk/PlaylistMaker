package com.example.playlistmaker.presentation.main

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.MediaActivity

class MainPresenter(
    private val view: MainView,
    private val context: Context
) {

    fun onCreate() {

        view.onMediaButtonTap {
            val mediaActivity = Intent(context, MediaActivity::class.java)
            context.startActivity(mediaActivity)
        }

        view.onSearchButtonTap {
            val searchActivity = Intent(context, SearchActivity::class.java)
            context.startActivity(searchActivity)
        }

        view.onSettingsButtonTap {
            val settingsActivity = Intent(context, SettingsActivity::class.java)
            context.startActivity(settingsActivity)
        }
    }
}