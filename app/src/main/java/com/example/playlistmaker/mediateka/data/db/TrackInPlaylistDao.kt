package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackInPlaylistEntity: TrackInPlaylistEntity)
}