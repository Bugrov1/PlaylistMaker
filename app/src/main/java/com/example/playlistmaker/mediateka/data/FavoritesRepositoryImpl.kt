package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.mediateka.data.db.TrackEntity
import com.example.playlistmaker.mediateka.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl
    (private val appDatabase: AppDatabase,
     private val trackDbConvertor: TrackDbConvertor) : FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracksEntities = appDatabase.trackDao().getTracks()
        val tracks = tracksEntities.map { track -> trackDbConvertor.map(track) }
        emit(tracks)
    }

    override suspend fun getIds():List<Int> {
        val tracksIds = appDatabase.trackDao().getTracksId()
        return  tracksIds
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }




}