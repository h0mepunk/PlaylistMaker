package com.example.playlistmaker.di

import com.example.playlistmaker.services.track.MusicService
import org.koin.dsl.module

val serviceModule = module {

    single<MusicService> {
        MusicService()
    }
}