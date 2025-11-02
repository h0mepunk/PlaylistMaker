package com.example.playlistmaker.presentation

import android.app.Activity
import android.content.Intent
import android.widget.Button
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.track.MediaActivity

class MainController(private val activity: Activity) {

    private lateinit var buttonSearch : Button
    private lateinit var buttonSettings : Button
    private lateinit var mediaButton : Button

    fun onCreate() {
        buttonSearch = activity.findViewById(R.id.search_button)
        buttonSettings = activity.findViewById(R.id.settings_button)
        mediaButton = activity.findViewById(R.id.media_button)

        buttonSearch.setOnClickListener {
            val searchActivity = Intent(activity, SearchActivity::class.java)
            activity.startActivity(searchActivity)
        }

        buttonSettings.setOnClickListener{
            val settingsActivity = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(settingsActivity)
        }

        mediaButton.setOnClickListener {
            val mediaActivity = Intent(activity, MediaActivity::class.java)
            activity.startActivity(mediaActivity)
        }
    }
}