package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository):FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(track: Track) {
       favoritesRepository.deleteFromFavorites(track)
    }

    override fun getTracks(): Flow<List<Track>> {
        return favoritesRepository.getTracks()
    }

    override suspend fun getIds(): List<Int> {
        return favoritesRepository.getIds()
    }
}