package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackDataProcessor {

    fun getTracksList(sharedPreferences: SharedPreferences): ArrayList<Track> {
        return tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")!!
        )
    }

    fun tracksListFromJson(json: String?): ArrayList<Track> {
        return if (json != null) {
            if (json.isEmpty()) {
                ArrayList()
            } else {
                val type = object : TypeToken<ArrayList<Track>>() {}.type
                Gson().fromJson(json, type)
            }
        } else {
            ArrayList()
        }
    }

    fun tracksListToJson(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    fun trackFromJson(json: String?): Track {
        val type = object : TypeToken<Track>() {}.type
        return Gson().fromJson(json, type)
    }

    fun trackToJson(track:Track): String {
        return Gson().toJson(track)
    }
}