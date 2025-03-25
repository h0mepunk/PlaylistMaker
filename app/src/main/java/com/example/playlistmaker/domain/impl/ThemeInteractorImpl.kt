package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.ThemeRepositoryImpl

class ThemeInteractorImpl(private val repository: ThemeRepository) {

    override fun setTheme(theme: String) {
        repository.setTheme(theme)
    }

    override fun getTheme(): String {
        return repository.getTheme()
    }
}