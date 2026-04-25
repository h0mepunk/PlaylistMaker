package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.presentation.search.SingleLiveEvent

class SettingsViewModel(): ViewModel() {

    private val stateLiveData = SingleLiveEvent<SettingsState>()
    fun observeState(): LiveData<SettingsState> = stateLiveData
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