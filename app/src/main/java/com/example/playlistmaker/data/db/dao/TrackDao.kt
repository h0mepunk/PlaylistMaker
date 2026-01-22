package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): TrackEntity

    @Delete
    suspend fun deleteTrack(track: TrackEntity): TrackEntity

    @Query("SELECT * FROM tracks_table")
    suspend fun getTracks(): List<TrackEntity>
}