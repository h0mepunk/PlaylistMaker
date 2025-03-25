package com.example.playlistmaker.data

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.ui.settings.THEME_SWITCH_KEY

class ThemeRepositoryImpl(private val creator: Creator): ThemeRepository {

    private val darkThemeDefault = false

    override fun getTheme(): Boolean {
        return creator.sharedPreferences.getBoolean(THEME_SWITCH_KEY, darkThemeDefault)
    }

    override fun setTheme(darkTheme: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        creator.sharedPreferences.edit()
            .putBoolean(THEME_SWITCH_KEY, darkTheme)
            .apply()
    }
}