package com.example.playlistmaker.ui.main

import android.app.Application
import com.example.playlistmaker.presentation.search.TracksSearchPresenter
import com.example.playlistmaker.util.Creator
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class App : Application() {

    var tracksSearchPresenter : TracksSearchPresenter? = null
    private val themeInteractor by lazy { Creator.provideThemeInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.context = applicationContext
        val theme = themeInteractor.getTheme()
        switchTheme(theme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        Creator.provideThemeInteractor().setTheme(darkThemeEnabled)
    }
}

