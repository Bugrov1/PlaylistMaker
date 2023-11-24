package com.example.playlistmaker.mediateka.domain.db


import com.example.playlistmaker.search.domain.models.Track
import java.util.concurrent.Flow

interface FavoritesRepository {

    fun addToFavorites(track: Track)
    fun deleteFromFavorites(track: Track)
    fun getTracks(): kotlinx.coroutines.flow.Flow<List<Track>>
}