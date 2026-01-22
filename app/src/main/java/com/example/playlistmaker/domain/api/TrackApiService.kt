package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.dto.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search")
    suspend fun getTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): TrackResponse
}