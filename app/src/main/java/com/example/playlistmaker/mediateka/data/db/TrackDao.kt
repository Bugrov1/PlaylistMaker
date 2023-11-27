package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorites_table order by timeStamp DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites_table")
    suspend fun getTracksId(): List<Int>

}