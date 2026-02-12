package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistsTracksEntity

@Dao
interface PlaylistTracksDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: PlaylistsTracksEntity)

    @Query("SELECT * FROM playlists_tracks_table WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: Int): PlaylistsTracksEntity

}