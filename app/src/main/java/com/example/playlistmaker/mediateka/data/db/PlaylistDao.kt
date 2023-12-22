package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.google.gson.Gson

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(playlistEntity: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(playlistEntity: PlaylistEntity)

    @Query("SELECT tracks FROM playlist_table WHERE id = :id")
    suspend fun getTracksId(id:Long): String

    @Query("SELECT * FROM playlist_table ")
    suspend fun getLists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getPlaylist(id:Long):PlaylistEntity

    @Query("SELECT tracks FROM playlist_table ")
    suspend fun  getAll():List<String>

    @Delete(entity = PlaylistEntity::class)
    suspend fun delete(playlistEntity: PlaylistEntity)



}