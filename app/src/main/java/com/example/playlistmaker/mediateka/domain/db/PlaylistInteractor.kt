package com.example.playlistmaker.mediateka.domain.db

import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun insertList(playlist: Playlist)

    suspend fun updateList(playlist: Playlist)

    suspend fun getTracksId(id:Long): String

    fun getLists():Flow<List<Playlist>>
    suspend fun insertTrack(track: Track)

    suspend fun getPlaylist(id: Long):Playlist

    fun getTrackListFlow(ids: List<Int>):Flow<List<Track>>

    suspend fun getTracksListIds():List<String>

    suspend fun checkTrack(track: Track):Boolean

    suspend fun deleteTrack(track: Track)
     suspend fun deletePlaylist(playlist: Playlist)

    suspend fun getIdsFromAddedTracks(): List<Int>
    suspend fun deleteById(id:Int)

    suspend fun updateTracksTable()
}