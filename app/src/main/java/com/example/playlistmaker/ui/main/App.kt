package com.example.playlistmaker.ui.main

import android.app.Application
import com.example.playlistmaker.Creator

class App : Application() {

    private val creator = Creator
    private val themeInteractor = creator.provideThemeInteractor()

    override fun onCreate() {
        super.onCreate()
        creator.context = this
        val theme = themeInteractor.getTheme()
        switchTheme(theme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeInteractor.setTheme(darkThemeEnabled)
    }
}

