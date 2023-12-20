package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository):PlaylistInteractor {
    override suspend fun insertList(playlist: Playlist) {
        playlistRepository.insertList(playlist)
    }

    override suspend fun updateList(playlist: Playlist) {
        playlistRepository.updateList(playlist)
    }

    override suspend fun getTracksId(id: Long): String {
        return playlistRepository.getTracksId(id)

    }

    override fun getLists(): Flow<List<Playlist>> {
        return playlistRepository.getLists()
    }

    override suspend fun insertTrack(track: Track) {
        playlistRepository.insertTrack(track)
    }

    override suspend fun getPlaylist(id: Long):Playlist {
        return playlistRepository.getPlaylist(id)
    }

    override fun getracks(ids: List<Int>): Flow<List<Track>> {
        return playlistRepository.getracks(ids)
    }


}