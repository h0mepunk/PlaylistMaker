package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(): ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>()
    fun observeState(): LiveData<SettingsState> = stateLiveData
    fun switchTheme(isDarkTheme: Boolean) {
            renderState(SettingsState.Theme(isDarkTheme))
        }

    fun clickShareApp() {
           renderState(SettingsState.Share)
        }

    fun clickContactSupport() {
            renderState(SettingsState.Support)
        }

    fun clickUserAgreement() {
            renderState(SettingsState.UserAgreement)
        }

    private fun renderState(state: SettingsState) {
        stateLiveData.postValue(state)
    }

}