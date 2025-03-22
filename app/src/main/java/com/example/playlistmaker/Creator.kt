package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl

class Creator(private val context: Context) {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getTrackHistory(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTrackHistory())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}