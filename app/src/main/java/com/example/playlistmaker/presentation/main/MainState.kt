package com.example.playlistmaker.presentation.main

sealed interface MainState {

    object Track: MainState

    object Settings: MainState

    object Search: MainState
}