package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.Const.CURRENT_TRACK_KEY
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("task_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getTracksList(sharedPreferences: SharedPreferences): ArrayList<Track> {
        val tracks = tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, ""))
        Log.e("???? track list got ${tracks.size}", tracks.toString())
        return tracksListFromJson(
            sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")
        )
    }

    fun saveTracksList(tracks: ArrayList<Track>) {
        Log.e("???? track list saved ${tracks.size}", tracks.toString())
        sharedPreferences.edit()
            .putString(
                TRACK_HISTORY_LIST_KEY,
                tracksListToJson(tracks)
            )
            .apply()
    }

    fun saveCurrentTrack(track: Track) {
        Log.e("???? track saved", track.toString())
        sharedPreferences.edit()
            .putString(CURRENT_TRACK_KEY, trackToJson(track))
            .apply()
    }

    fun getCurrentTrack(): Track {
        Log.e("???? track got", trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, "")).toString())
        return trackFromJson(sharedPreferences.getString(
            CURRENT_TRACK_KEY, ""))
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

    private fun trackFromJson(json: String?): Track {
        val type = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, type)
    }

    private fun trackToJson(track: Track): String {
        return gson.toJson(track)
    }
}