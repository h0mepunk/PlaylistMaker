package com.example.playlistmaker.presentation.main

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openSearch()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openSettings()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openMedia()
}