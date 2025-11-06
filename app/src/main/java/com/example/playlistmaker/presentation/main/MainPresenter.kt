package com.example.playlistmaker.presentation.main

import moxy.MvpPresenter

class MainPresenter(): MvpPresenter<MainView>() {

    fun onSearchClicked()  = viewState.openSearch()
    fun onSettingsClicked() = viewState.openSettings()
    fun onMediaClicked()    = viewState.openMedia()
}