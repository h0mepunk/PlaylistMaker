package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.Const.CURRENT_TRACK_KEY
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.reflect.TypeToken

class TracksHistoryRepositoryImpl(private val creator: Creator): TracksHistoryRepository {

    override fun getTracksHistory(): ArrayList<Track> {
        val tracks = tracksListFromJson(
            creator.sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, ""))
        Log.e("???? track list got ${tracks.size}", tracks.toString())
        return tracksListFromJson(
            creator.sharedPreferences.getString(TRACK_HISTORY_LIST_KEY, "")
        )
    }

    override fun saveTracksHistory(tracks: ArrayList<Track>) {
        Log.e("???? track list saved ${tracks.size}", tracks.toString())
        creator.sharedPreferences.edit()
            .putString(
                TRACK_HISTORY_LIST_KEY,
                tracksListToJson(tracks)
            )
            .apply()
    }

    override fun saveCurrentTrack(track: Track) {
        Log.e("???? track saved", track.toString())
        creator.sharedPreferences.edit()
            .putString(CURRENT_TRACK_KEY, trackToJson(track))
            .apply()
    }

    override fun getCurrentTrack(): Track {
        Log.e("???? track got", trackFromJson(creator.sharedPreferences.getString(
            CURRENT_TRACK_KEY, "")).toString())
        return trackFromJson(creator.sharedPreferences.getString(
            CURRENT_TRACK_KEY, ""))
    }

    private fun tracksListFromJson(json: String?): ArrayList<Track> {
        return if (json != null) {
            if (json.isEmpty()) {
                ArrayList()
            } else {
                val type = object : TypeToken<ArrayList<Track>>() {}.type
                creator.gson.fromJson(json, type)
            }
        } else {
            ArrayList()
        }
    }

    private fun tracksListToJson(tracks: ArrayList<Track>): String {
        return creator.gson.toJson(tracks)
    }

    private fun trackFromJson(json: String?): Track {
        val type = object : TypeToken<Track>() {}.type
        return creator.gson.fromJson(json, type)
    }

    private fun trackToJson(track: Track): String {
        return creator.gson.toJson(track)
    }
}