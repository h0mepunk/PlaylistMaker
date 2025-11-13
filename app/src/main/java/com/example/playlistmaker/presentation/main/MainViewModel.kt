package com.example.playlistmaker.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(): ViewModel() {

    private val stateLiveData = MutableLiveData<MainState>()
    fun observeState(): LiveData<MainState> = stateLiveData

    fun mediaButtonTap() {
        renderState(MainState.Track)
    }

    fun searchButtonTap() {
        renderState(MainState.Search)
    }

    fun settingsButtonTap() {
        renderState(MainState.Settings)
    }

    private fun renderState(state: MainState) {
        stateLiveData.postValue(state)
    }
}