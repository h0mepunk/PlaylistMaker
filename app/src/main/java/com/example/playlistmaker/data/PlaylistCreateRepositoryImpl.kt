package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.Const.CURRENT_PLAYLIST_CREATION_KEY
import com.example.playlistmaker.Const.CURRENT_PLAYLIST_KEY
import com.example.playlistmaker.Const.EMPTY_STRING
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

    override fun getCurrentCreatingPlaylist(): Playlist? =
        getPlaylistFromSharedPrefs(CURRENT_PLAYLIST_CREATION_KEY)

    override fun saveCurrentCreatingPlaylist(playlist: Playlist?) =
        savePlaylistToSharedPrefs(CURRENT_PLAYLIST_CREATION_KEY, playlist)

    override fun getCurrentPlaylist(): Playlist?=
        getPlaylistFromSharedPrefs(CURRENT_PLAYLIST_KEY)

    override fun saveCurrentPlaylist(playlist: Playlist?) =
        savePlaylistToSharedPrefs(CURRENT_PLAYLIST_KEY, playlist)

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

    private fun getPlaylistFromSharedPrefs(key: String): Playlist? {
        val json = sharedPreferences.getString(key, "")
        Log.i(LOG_TAG, "get playlist from shared prefs $json")
        return playlistFromJson(json)
    }

    private fun savePlaylistToSharedPrefs(key: String, playlist: Playlist?) {
        {
            Log.i(LOG_TAG, "playlist saved $playlist")
            if (playlist!= null) {
                Log.i(LOG_TAG, "playlist is not null")
                sharedPreferences.edit()
                    .putString(
                        key,
                        playlistToJson(playlist!!)
                    )
                    .apply()
            } else {
                Log.i(LOG_TAG, "playlist is null")
                sharedPreferences.edit()
                    .putString(
                        key,
                        EMPTY_STRING
                    )
            }
        }
    }
}