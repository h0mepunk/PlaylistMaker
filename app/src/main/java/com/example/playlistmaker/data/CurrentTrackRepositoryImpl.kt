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
    override fun saveCurrentTrack(track: Track) {
        Log.i("TracksHistoryRepository track saved", track.toString())
        sharedPreferences.edit()
            .putString(CURRENT_TRACK_KEY, trackToJson(track))
            .apply()
    }

    override fun getCurrentTrack(): Track {
        Log.i("TracksHistoryRepository track got", trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, "")).toString())
        return trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, ""))
    }

    private fun trackFromJson(json: String?): Track {
        val type = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, type)
    }

    private fun trackToJson(track: Track): String {
        return gson.toJson(track)
    }
}