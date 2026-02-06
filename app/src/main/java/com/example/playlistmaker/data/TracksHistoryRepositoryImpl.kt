package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
): TracksHistoryRepository {

    private val LOG_TAG = "TracksHistoryRepository"

    override fun getTracksHistory(): ArrayList<Track> {
        val tracks = tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, ""))
        Log.i(LOG_TAG,"track list got ${tracks.size} + $tracks")
        return tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")
        )
    }

    override fun saveTracksHistory(tracks: ArrayList<Track>) {
        Log.i(LOG_TAG, "track list saved ${tracks.size} + $tracks")
        sharedPreferences.edit()
            .putString(
                TRACK_HISTORY_LIST_KEY,
                tracksListToJson(tracks)
            )
            .apply()
    }

    private fun tracksListFromJson(json: String?): ArrayList<Track> {
        return if (json != null) {
            if (json.isEmpty()) {
                ArrayList()
            } else {
                val type = object : TypeToken<ArrayList<Track>>() {}.type
                gson.fromJson(json, type)
            }
        } else {
            ArrayList()
        }
    }

    private fun tracksListToJson(tracks: ArrayList<Track>): String {
        return gson.toJson(tracks)
    }
}