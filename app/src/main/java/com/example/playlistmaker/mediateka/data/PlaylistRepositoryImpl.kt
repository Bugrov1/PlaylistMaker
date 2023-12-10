package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.converters.PlaylistDBConvertor
import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.data.db.PlaylistEntity
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val playlistDatabase: PlaylistDatabase,
                             private val playlistDbConvertor: PlaylistDBConvertor
): PlaylistRepository {
    override suspend fun insertList(playlist: Playlist) {
        val listEntity = playlistDbConvertor.map(playlist)
        playlistDatabase.PlaylistDao().insertList(listEntity)
    }

    override suspend fun updateList(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksId(id: Long): String {
        return playlistDatabase.PlaylistDao().getTracksId(id)
    }

    override fun getLists(): Flow<List<Playlist>> = flow {
        val playlistEntities = playlistDatabase.PlaylistDao().getLists()
        val lists = playlistEntities.map { list -> playlistDbConvertor.map(list) }
        emit(lists)
    }


}