package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.TrackApiService
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApiService = retrofit.create(TrackApiService::class.java)

    fun loadTracks(text:String, callback: Callback<TrackResponse>) {
        tracksApiService.getTracks(text).enqueue(callback)
    }
}