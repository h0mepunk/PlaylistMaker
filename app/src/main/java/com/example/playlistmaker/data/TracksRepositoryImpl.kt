package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TracksSearchRequest

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksMapper: TrackMapper
    ) : TracksRepository {

    override fun searchTracks(
        text: String,
    ): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        if (response.resultCode == 200) {
            Log.e("????? tracks list", response.toString())
            return tracksMapper.mapTrackDtoListToTrackList((response as TrackResponse).results)
        } else {
            return emptyList()
        }
    }
}