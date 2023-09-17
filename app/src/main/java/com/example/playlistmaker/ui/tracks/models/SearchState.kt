package com.example.playlistmaker.ui.tracks.models

import com.example.playlistmaker.domain.Track

sealed interface SearchState{

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState

    data class History(
        val history: Array<Track>?
    ) : SearchState

}