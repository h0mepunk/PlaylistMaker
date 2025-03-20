package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search")
    fun getTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>
}