package com.example.playlistmaker.mediateka.ui.models

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState

sealed interface FavoritesState{

    object Empty :  FavoritesState

    data class Content(
        val tracks: List<Track>
    ) :  FavoritesState
}