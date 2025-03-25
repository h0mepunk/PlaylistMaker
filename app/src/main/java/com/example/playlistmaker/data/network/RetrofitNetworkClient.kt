package com.example.playlistmaker.data.network

import com.example.playlistmaker.Creator
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val creator: Creator) : NetworkClient {

    private val tracksApiService = creator.tracksApiService

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val resp = tracksApiService.getTracks(dto.searchText).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}