package com.example.playlistmaker.ui.track

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.TrackController

class MediaActivity : AppCompatActivity() {

    private lateinit var trackController : TrackController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackController = Creator.provideTrackController(this)
        setContentView(R.layout.activity_media)
        trackController.onCreate()
    }

    override fun onPause() {
        super.onPause()
        trackController.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackController.onDestroy()
    }
}