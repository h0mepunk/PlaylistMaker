package com.example.playlistmaker.presentation.settings

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SettingsView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openShareApp()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun contactSupport()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openUserAgreement()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun switchTheme(isDarkMode: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setTheme(isDarkMode: Boolean)
}