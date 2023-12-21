package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.converters.PlaylistDBConvertor
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.data.db.TracksInPlaylistsDatabase
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val playlistDatabase: PlaylistDatabase,
                             private val playlistDbConvertor: PlaylistDBConvertor,
                             private val tracksInPlaylistsDatabase: TracksInPlaylistsDatabase
): PlaylistRepository {
    override suspend fun insertList(playlist: Playlist) {
        val listEntity = playlistDbConvertor.map(playlist)
        playlistDatabase.PlaylistDao().insertList(listEntity)
    }

    override suspend fun updateList(playlist: Playlist) {
        val listEntity = playlistDbConvertor.mapUpdate(playlist)
        playlistDatabase.PlaylistDao().updateList(listEntity)
    }

    override suspend fun getTracksId(id: Long): String {
        return playlistDatabase.PlaylistDao().getTracksId(id)
    }

    override fun getLists(): Flow<List<Playlist>> = flow {
        val playlistEntities = playlistDatabase.PlaylistDao().getLists()
        val lists = playlistEntities.map { list -> playlistDbConvertor.map(list) }
        emit(lists)
    }

    override suspend fun insertTrack(track: Track) {
        val trackEntities = playlistDbConvertor.map(track)
        tracksInPlaylistsDatabase.trackInPlaylistDao().insertTrack(trackEntities)
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        val playlistEntity = playlistDatabase.PlaylistDao().getPlaylist(id)
       return playlistDbConvertor.map(playlistEntity)
    }

    override fun getracks(ids: List<Int>): Flow<List<Track>>  = flow{
        val tracktEntities = tracksInPlaylistsDatabase.trackInPlaylistDao().getracks(ids)
        val tracks = tracktEntities.map { track -> playlistDbConvertor.map(track) }
        emit(tracks)
    }

    override suspend fun getAll():List<String> {
        return playlistDatabase.PlaylistDao().getAll()
    }

    override suspend fun deleteTrack(track: Track){
        val trackEntity = playlistDbConvertor.map(track)
        tracksInPlaylistsDatabase.trackInPlaylistDao().deleteTrack(trackEntity)

    }


}