package com.example.playlistmaker.presentation.track

import android.widget.ImageView

interface TrackView {

    fun setPlayButtonActive(active: Boolean)

    fun getPlaceholderImageView(): ImageView

    fun setTrackTimeText(text: String)

    fun enablePlayButton(enabled: Boolean)

}