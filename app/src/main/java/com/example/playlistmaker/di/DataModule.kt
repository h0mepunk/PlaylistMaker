package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.Const
import com.example.playlistmaker.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TrackMapper
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackApiService
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<TrackApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(
                Const.PLAYLIST_MAKER_PREFERENCES,
                Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single{
        TrackMapper()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single {
        MediaPlayer()
    }

    single<TracksRepository>{
        TracksRepositoryImpl(get(),get())
    }

    single<TracksHistoryRepository>{
        TracksHistoryRepositoryImpl(get(), get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

    single <MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }
}