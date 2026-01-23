package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylists(): List<PlaylistEntity>
}