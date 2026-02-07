package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.Const.CURRENT_PLAYLIST_KEY
import com.example.playlistmaker.Const.TRACK_HISTORY_LIST_KEY
import com.example.playlistmaker.domain.api.PlaylistCreateRepository
import com.example.playlistmaker.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistCreateRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
): PlaylistCreateRepository {

    private val LOG_TAG = "PlaylistCreateRepository"

    override fun getCurrentPlaylist(): Playlist? {
        val playlist = playlistFromJson(
            sharedPreferences.getString(CURRENT_PLAYLIST_KEY, "")
        )
        Log.i(LOG_TAG, "track list got $playlist")
        return playlistFromJson(
            sharedPreferences.getString(CURRENT_PLAYLIST_KEY, "")
        )
    }

    override fun saveCurrentPlaylist(playlist: Playlist) {
        Log.i(LOG_TAG, "playlist saved $playlist")
        sharedPreferences.edit()
            .putString(
                TRACK_HISTORY_LIST_KEY,
                playlistToJson(playlist)
            )
            .apply()
    }

    private fun playlistFromJson(json: String?): Playlist? {
        return if (json != null) {
            if (json.isEmpty()) {
                null
            } else {
                val type = object : TypeToken<Playlist>() {}.type
                gson.fromJson(json, type)
            }
        } else {
            null
        }
    }

    private fun playlistToJson(playlist: Playlist): String {
        return gson.toJson(playlist)
    }
}