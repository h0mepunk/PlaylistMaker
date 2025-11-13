package com.example.playlistmaker.presentation.settings

sealed interface SettingsState {

    object UserAgreement: SettingsState

    object Share: SettingsState

    class Theme(val isDarkThemeEnabled: Boolean): SettingsState

    object Support : SettingsState

}