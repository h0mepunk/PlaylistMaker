package com.example.playlistmaker.data.db.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.CurrentDao
import com.example.playlistmaker.data.db.dao.HistoryDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao

@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun currentDao(): CurrentDao

    abstract fun historyDao(): HistoryDao

}