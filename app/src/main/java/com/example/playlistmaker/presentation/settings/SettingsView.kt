package com.example.playlistmaker.presentation.settings

interface SettingsView {

    fun clickShareApp(action: () -> Unit)

    fun clickContactSupport(action: () -> Unit)

    fun clickUserAgreement(action: () -> Unit)

    fun switchTheme(action: (isDarkMode: Boolean) -> Unit)

    fun setTheme(isDarkMode: Boolean)
}