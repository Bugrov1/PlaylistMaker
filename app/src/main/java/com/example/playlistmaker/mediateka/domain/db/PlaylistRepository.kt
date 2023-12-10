package com.example.playlistmaker.mediateka.domain.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.mediateka.data.db.PlaylistEntity
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import java.util.concurrent.Flow

interface PlaylistRepository {

    suspend fun insertList(playlist: Playlist)

    suspend fun updateList(playlist: Playlist)

    suspend fun getTracksId(id: Long): String

    fun getLists(): kotlinx.coroutines.flow.Flow<List<Playlist>>
}