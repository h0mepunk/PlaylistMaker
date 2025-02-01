package com.example.playlistmaker

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackDataProcesser {

    fun tracksListFromJson(json: String?): ArrayList<Track> {
        return if (json != null) {
            if (json.isEmpty()) {
                Log.e("?????", "json is empty 0")
                ArrayList()
            } else {
                Log.e("?????", "json is  not empty, json: $json")
                val type = object : TypeToken<ArrayList<Track>>() {}.type
                Gson().fromJson(json, type)
            }
        } else {
            Log.e("?????", "json is empty 1")
            ArrayList()
        }
    }

    fun tracksListToJson(tracks: ArrayList<Track>): String {
        Log.e("?????", "json : ${Gson().toJson(tracks)}")
        return Gson().toJson(tracks)
    }
}