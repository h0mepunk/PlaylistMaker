package com.example.playlistmaker.ui.search

sealed interface ToastState {
    object None: ToastState
    data class Show(val additionalMessage: String): ToastState
}