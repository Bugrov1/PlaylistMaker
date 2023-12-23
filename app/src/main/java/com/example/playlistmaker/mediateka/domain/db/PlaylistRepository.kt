package com.example.playlistmaker.mediateka.domain.db

import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistRepository {

    suspend fun insertList(playlist: Playlist)

    suspend fun updateList(playlist: Playlist)

    suspend fun getTracksId(id: Long): String

    fun getLists(): kotlinx.coroutines.flow.Flow<List<Playlist>>

    suspend fun insertTrack(track: Track)

    suspend fun getPlaylist(id: Long):Playlist

    fun getracks(ids: List<Int>): kotlinx.coroutines.flow.Flow<List<Track>>

    suspend fun getAll():List<String>

    suspend fun deleteTrack(track: Track)

    suspend fun delete(playlist: Playlist)

    suspend fun getIdsFromAddedTracks(): List<Int>

    suspend fun deleteById(id:Int)
}