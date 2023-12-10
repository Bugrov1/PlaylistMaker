package com.example.playlistmaker.mediateka.domain.db

import com.example.playlistmaker.mediateka.domain.model.Playlist

interface PlaylistInteractor {
    suspend fun insertList(playlist: Playlist)

    suspend fun updateList(playlist: Playlist)

    suspend fun getTracksId(id:Long): String

    fun getLists():kotlinx.coroutines.flow.Flow<List<Playlist>>
}