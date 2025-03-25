package com.example.playlistmaker.ui.main

import android.app.Application
import com.example.playlistmaker.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.context = applicationContext
        val themeInteractor = Creator.provideThemeInteractor()
        val theme = themeInteractor.getTheme()
        switchTheme(theme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        Creator.provideThemeInteractor().setTheme(darkThemeEnabled)
    }
}

