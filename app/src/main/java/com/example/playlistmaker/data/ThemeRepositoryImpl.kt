package com.example.playlistmaker.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Const.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.ui.settings.THEME_SWITCH_KEY

class ThemeRepositoryImpl(private val context: Context): ThemeRepository {

    private val sharedPreferences = context.getSharedPreferences(
        PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    private val darkThemeDefault = false

    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_SWITCH_KEY, darkThemeDefault)
    }

    override fun setTheme(darkTheme: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit()
            .putBoolean(THEME_SWITCH_KEY, darkTheme)
            .apply()
    }
}