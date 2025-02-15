package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackDataProcesser {

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
}