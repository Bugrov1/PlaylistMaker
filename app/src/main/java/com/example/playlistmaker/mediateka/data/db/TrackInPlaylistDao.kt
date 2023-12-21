package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackInPlaylistEntity: TrackInPlaylistEntity)


    @Query("SELECT * FROM tracks_in_playlists_table WHERE trackId IN (:ids)")
    suspend fun getracks(ids: List<Int>): List<TrackInPlaylistEntity>

    @Delete(entity = TrackInPlaylistEntity::class)
    suspend fun deleteTrack(trackInPlaylistEntity: TrackInPlaylistEntity)
}