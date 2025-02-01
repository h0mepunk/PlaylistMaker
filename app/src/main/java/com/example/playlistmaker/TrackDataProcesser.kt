package com.example.playlistmaker

import com.google.gson.Gson

class TrackDataProcesser {

    fun tracksListFromJson(json: String): Array<Track> {
        return if (json.isEmpty()) {
            emptyArray()
        } else {
            Gson().fromJson(json, Array<Track>::class.java)
        }
    }

    fun tracksListToJson(tracks: Array<Track>): String {
        return Gson().toJson(tracks)
    }
}