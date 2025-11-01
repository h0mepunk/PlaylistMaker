package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.util.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksMapper: TrackMapper
    ) : TracksRepository {

    override fun searchTracks(
        text: String,
    ): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        return when(response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success(tracksMapper.mapTrackDtoListToTrackList((response as TrackResponse).results))
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}