package com.example.playlistmaker.ui.main

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.util.Creator
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import kotlin.getValue

class App : Application() {
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        val theme = themeInteractor.getTheme()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        switchTheme(theme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeInteractor.setTheme(darkThemeEnabled)
    }
}

