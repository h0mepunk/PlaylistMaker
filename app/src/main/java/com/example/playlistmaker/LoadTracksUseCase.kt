package com.example.playlistmaker

import android.util.Log
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadTracksUseCase(
    private val tracksRepository: TracksInteractor,
    private val tracksNetworkClient: RetrofitNetworkClient,
    private val searchText: String
) {

    fun execute(
        searchText: String, // searchText?
    ) {
        tracksRepository.searchTracks(searchText, object : TracksInteractor.TracksConsumer {

        })

//        tracksNetworkClient.loadTracks(searchText, object: Callback<TrackResponse> {
//            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
//                if (response.code() == 200) {
//                    val trackDtoList = response.body()?.results ?: emptyList()
//                    Log.e("????? dtoList", trackDtoList.toString())
//                    val tracks: ArrayList<Track> = ArrayList()
//                    trackDtoList.map {
//                        tracks.add(
//                            Track(
//                            it.trackName,
//                            it.artistName,
//                            it.trackTimeMillis, // форматировать в ммсс
//                            it.artworkUrl100,
//                            it.trackId,
//                            it.releaseDate,
//                            it.primaryGenreName,
//                            it.country,
//                            it.collectionName,
//                            it.previewUrl)
//                        )
//                    }
//                    Log.e("????? tracks list", tracks.toString())
//                    onSuccess(tracks)
//                } else {
//                    onErrorResponse(response.errorBody()?.string() ?: "")
//                }
//            }
//
//            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
//                onError(t)
//            }
//        })
    }
}