package com.example.playlistmaker.ui.search

import android.app.Application
import com.example.playlistmaker.presentation.search.TracksSearchPresenter

class TracksApplication: Application() {
    var tracksSearchPresenter: TracksSearchPresenter? = null
}