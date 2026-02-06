package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.Const.CURRENT_TRACK_KEY
import com.example.playlistmaker.domain.api.CurrentTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CurrentTrackRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
): CurrentTrackRepository {
    private val LOG_TAG = "CurrentTrackRepository"
    override fun saveCurrentTrack(track: Track) {
        Log.i(LOG_TAG, "track saved $track")
        sharedPreferences.edit()
            .putString(CURRENT_TRACK_KEY, trackToJson(track))
            .apply()
    }

    override fun getCurrentTrack(): Track {
        val track = trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, ""))
        Log.i(LOG_TAG, " track got $track")
        return track
    }

    private fun trackFromJson(json: String?): Track {
        val type = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, type)
    }

    private fun trackToJson(track: Track): String {
        return gson.toJson(track)
    }
}