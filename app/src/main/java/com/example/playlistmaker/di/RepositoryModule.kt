package com.example.playlistmaker.di

import com.example.playlistmaker.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.impl.PlaylistRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    single<ThemeRepository>{
        ThemeRepositoryImpl(get())
    }

    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
}