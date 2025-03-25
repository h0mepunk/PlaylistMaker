package com.example.playlistmaker.ui.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.settings.THEME_SWITCH_KEY

class App : Application() {

    private val themeInteractor = Creator(this).provideThemeInteractor()

    override fun onCreate() {
        super.onCreate()
        val theme = themeInteractor.getTheme()
        switchTheme(theme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeInteractor.setTheme(darkThemeEnabled)
    }
}

