package com.example.playlistmaker.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.Const
import com.example.playlistmaker.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TrackMapper
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackApiService
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.presentation.MainController
import com.example.playlistmaker.presentation.SettingsController
import com.example.playlistmaker.presentation.TrackController
import com.example.playlistmaker.presentation.TracksSearchController
import com.example.playlistmaker.ui.track.TrackAdapter
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private val tracksMapper = TrackMapper()

    lateinit var context: Context
    val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences(
        Const.PLAYLIST_MAKER_PREFERENCES,
        Context.MODE_PRIVATE
    )}

    val gson = Gson()

    private val baseUrl = "https://itunes.apple.com"

    private var mediaPlayer = MediaPlayer()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tracksApiService = retrofit.create(TrackApiService::class.java)


    fun provideTracksSearchController(activity: Activity, adapter: TrackAdapter): TracksSearchController {
        return TracksSearchController(activity, adapter)
    }

    fun provideTrackController(activity: Activity): TrackController {
        return TrackController(activity)
    }

    fun provideSettingsController(activity: Activity): SettingsController {
        return SettingsController(activity)
    }

    fun provideMainController(activity: Activity): MainController {
        return MainController(activity)
    }
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(tracksApiService, context),
            tracksMapper
        )
    }

    private fun getTrackHistory(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(sharedPreferences, gson)
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(sharedPreferences)
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(MediaPlayer()) // если юзать один медиаплеер то при повторном открытии ему ПИЗДЕЦ
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTrackHistory())
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }
}