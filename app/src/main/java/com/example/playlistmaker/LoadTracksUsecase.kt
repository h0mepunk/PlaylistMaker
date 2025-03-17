package com.example.playlistmaker

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadTracksUsecase(
    private val tracksNetworkClient: TrackNetworkClient,
    private val trackManager: TrackManager,
    private val searchText: String
) {
    fun execute(
        onSuccess: (List<Track>) -> Unit,
        onErrorResponse: (errorText: String?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tracksNetworkClient.loadTracks(searchText, object: Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.code() == 200) {
                    val trackList = response.body()?.results as ArrayList<Track>
                    onSuccess(trackList)
                } else {
                    onErrorResponse(response.errorBody()?.string())
                }
//                if (response.code() == 200) {
//                    progressBar.visibility = View.GONE
//                    placeholderMessage.visibility = View.GONE
//                    trackList.clear()
//                    trackList.addAll(response.body()?.results as ArrayList<Track>)
//                    if (trackList.isEmpty()) {
//                        showMessage(
//                            text = R.string.empty_song_list_error_text,
//                            additionalMessage = "",
//                            buttonVisibility = View.GONE,
//                            icon = R.drawable.empty_results_error
//                        )
//                    } else {
//                        adapter.notifyDataSetChanged()
//                        songListRecycler.visibility = View.VISIBLE
//                        searchHistoryLayout.visibility = View.GONE
//                    }
//                } else {
//                    progressBar.visibility = View.GONE
//                    val errorJson = response.errorBody()?.string()
//                    showMessage(
//                        text = R.string.network_error_text,
//                        additionalMessage = errorJson.toString(),
//                        buttonVisibility = View.VISIBLE,
//                        icon = R.drawable.internet_error
//                    )
//                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                onError(t)
//                progressBar.visibility = View.GONE
//                t.printStackTrace()
//                showMessage(
//                    text = R.string.network_error_text,
//                    additionalMessage = "",
//                    buttonVisibility = View.VISIBLE,
//                    icon = R.drawable.internet_error
//                )
            }
        })
    }
}