package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun setTheme(darkTheme: Boolean)

    fun getTheme(): Boolean
}