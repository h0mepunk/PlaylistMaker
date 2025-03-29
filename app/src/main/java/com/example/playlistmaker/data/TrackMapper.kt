package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackMapper {

    fun mapTrackDtoListToTrackList(tracksDtoArrayList: List<TrackDto>): List<Track> {
        return tracksDtoArrayList.map {
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
    }
}