package com.example.playlistmaker

import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackNetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApiService = retrofit.create(TrackApiService::class.java)

    fun loadTracks(text:String, callback: Callback<TrackResponse>) {
        tracksApiService.getTracks(text).enqueue(callback)
    }
}