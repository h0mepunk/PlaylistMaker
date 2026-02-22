package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(track: PlaylistEntity)

    @Query("SELECT * FROM playlists_table WHERE id = :id LIMIT 1")
    suspend fun getPlaylistById(id: Int): PlaylistEntity

    @Query("""
        UPDATE playlists_table
        SET tracks = CASE WHEN tracks = '' THEN :newTrackId ELSE tracks || ',' || :newTrackId END,
            tracksCount = tracksCount + 1,
            timeTotal = timeTotal + :trackTime
        WHERE id = :id
    """)
    suspend fun addTrackToPlaylist(
        id: Int,
        newTrackId: String,
        trackTime: Long
    )

    @Query("""DELETE FROM playlists_table WHERE id = :id""")
    suspend fun deletePlaylist(id: Int)

    @Query("""
        UPDATE playlists_table
        SET tracks = 
            REPLACE(
                REPLACE(
                    REPLACE(tracks, ',' || :trackId, ''), 
                    :trackId || ',', ''), 
                :trackId, ''
            ),
            tracksCount = tracksCount - 1,
            timeTotal = timeTotal - :trackTime
        WHERE id = :id
    """)
    suspend fun deleteTrackFromPlaylist(
        id: Int,
        trackId: Int,
        trackTime: Long
    )
}