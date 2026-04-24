package com.example.playlistmaker.di

import com.example.playlistmaker.data.CurrentTrackRepositoryImpl
import com.example.playlistmaker.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.PlaylistCreateRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entity.AppDatabase
import com.example.playlistmaker.domain.api.CurrentTrackRepository
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.impl.LibraryRepositoryImpl
import com.example.playlistmaker.domain.impl.PlaylistRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
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

    single<PlaylistCreateRepository> {
        PlaylistCreateRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConverter() }

    factory { PlaylistTrackDbConverter() }

    single<PlaylistDao> { get<AppDatabase>().playlistDao() }

    single<TrackDao> { get<AppDatabase>().trackDao() }

    single<PlaylistTracksDao> { get<AppDatabase>().playlistTracksDao() }

    single<LibraryRepository> {
        LibraryRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }

    single < CurrentTrackRepository > {
        CurrentTrackRepositoryImpl(get(), get())
    }
}