package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository):PlaylistInteractor {
    override suspend fun insertList(playlist: Playlist) {
        playlistRepository.insertList(playlist)
    }

    override suspend fun updateList(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksId(id: Long): String {
        return playlistRepository.getTracksId(id)
    }

    override fun getLists(): Flow<List<Playlist>> {
        return playlistRepository.getLists()
    }


}