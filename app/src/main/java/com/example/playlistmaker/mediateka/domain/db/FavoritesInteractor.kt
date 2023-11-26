package com.example.playlistmaker.mediateka.domain.db

import com.example.playlistmaker.search.domain.models.Track

interface FavoritesInteractor {

    suspend fun addToFavorites(track: Track)
    suspend fun deleteFromFavorites(track: Track)
    fun getTracks(): kotlinx.coroutines.flow.Flow<List<Track>>

    suspend fun getIds():List<Int>
}