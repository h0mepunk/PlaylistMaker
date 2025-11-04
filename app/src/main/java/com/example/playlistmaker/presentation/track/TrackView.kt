package com.example.playlistmaker.presentation.track

import com.example.playlistmaker.ui.track.model.TrackState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TrackView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCover(url: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun enablePlayButton(enabled: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: TrackState)

}