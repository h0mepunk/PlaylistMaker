package com.example.playlistmaker

import android.util.Log
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.NetworkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadTracksUseCase(
    private val tracksNetworkClient: NetworkClient,
    private val searchText: String
) {
    fun execute(
        searchText: String,
        onSuccess: (ArrayList<Track>) -> Unit,
        onErrorResponse: (errorText: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tracksNetworkClient.loadTracks(searchText, object: Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    val trackDtoList = response.body()?.results ?: emptyList()
                    Log.e("????? dtoList", trackDtoList.toString())
                    val tracks: ArrayList<Track> = ArrayList()
                    trackDtoList.map {
                        tracks.add(
                            Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.collectionName,
                            it.previewUrl)
                        )
                    }
                    Log.e("????? tracks list", tracks.toString())
                    onSuccess(tracks)
                } else {
                    onErrorResponse(response.errorBody()?.string() ?: "")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}