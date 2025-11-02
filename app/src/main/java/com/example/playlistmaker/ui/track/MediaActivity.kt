package com.example.playlistmaker.ui.track

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R

class MediaActivity : AppCompatActivity() {
    private var mediaPlayer = Creator.provideMediaPlayerInteractor().getMediaPlayer()

    private val trackController = Creator.provideTrackController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        trackController.onCreate()
    }

    override fun onPause() {
        super.onPause()
        trackController.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}