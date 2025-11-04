package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.ui.track.model.TracksState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TracksSearchView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: TracksState)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(additionalMessage: String)
}