package com.example.playlistmaker.domain.api

import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.Track

interface MediaPlayerRepository {

    fun clickLike(currentTrack: Track)
}