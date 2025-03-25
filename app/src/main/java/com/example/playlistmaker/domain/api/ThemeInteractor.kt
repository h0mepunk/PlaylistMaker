package com.example.playlistmaker.domain.api

interface ThemeInteractor {

    fun getTheme(): Boolean

    fun setTheme(theme: Boolean)
}