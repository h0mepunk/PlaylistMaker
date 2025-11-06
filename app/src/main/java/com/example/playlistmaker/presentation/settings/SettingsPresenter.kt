package com.example.playlistmaker.presentation.settings

import com.example.playlistmaker.util.Creator
import moxy.MvpPresenter

class SettingsPresenter(): MvpPresenter<SettingsView>() {

    fun onCreate() {
        viewState.setTheme(Creator.provideThemeInteractor().getTheme())
    }

        fun switchTheme(isChecked: Boolean) = viewState.switchTheme(isChecked)

        fun share() = viewState.openShareApp()

        fun contactSupport() = viewState.contactSupport()

        fun openUserAgreement() = viewState.openUserAgreement()
}