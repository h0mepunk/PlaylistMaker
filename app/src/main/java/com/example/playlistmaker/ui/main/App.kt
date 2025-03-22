package com.example.playlistmaker.ui.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.ui.settings.THEME_SWITCH_KEY

class App : Application() {

    var darkTheme = false
    val sharedPrefs by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        darkTheme = sharedPrefs.getBoolean(THEME_SWITCH_KEY, darkTheme)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPrefs.edit()
            .putBoolean(THEME_SWITCH_KEY, darkThemeEnabled)
            .apply()
    }
}

