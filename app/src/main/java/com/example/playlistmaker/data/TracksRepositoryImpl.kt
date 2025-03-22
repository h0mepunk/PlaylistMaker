package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TracksSearchRequest
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(
        text: String,
    ): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        if (response.resultCode == 200) {
            Log.e("????? tracks list", response.toString())
            return (response as TrackResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis.toLong()),
                    it.artworkUrl100,
                    it.trackId,
                    SimpleDateFormat("YYYY", Locale.getDefault()).format(it.trackTimeMillis.toLong()),
                    it.primaryGenreName,
                    it.country,
                    it.collectionName,
                    it.previewUrl)
            }
        } else {
            return emptyList()
        }
    }
}