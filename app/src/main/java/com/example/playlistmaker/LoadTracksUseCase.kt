package com.example.playlistmaker

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadTracksUseCase(
    private val tracksNetworkClient: TrackNetworkClient,
    private val searchText: String
) {
    fun execute(
        onSuccess: (ArrayList<Track>) -> Unit,
        onErrorResponse: (errorText: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tracksNetworkClient.loadTracks(searchText, object: Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    val trackDtoList = response.body()?.results as ArrayList<TrackDto>
                    val tracks: ArrayList<Track> = ArrayList()
                    trackDtoList.forEach {
                        tracks.add(Track(
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