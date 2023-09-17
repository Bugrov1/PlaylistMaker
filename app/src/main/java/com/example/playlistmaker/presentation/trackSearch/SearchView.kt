package com.example.playlistmaker.presentation.trackSearch

import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.ui.tracks.models.SearchState

interface SearchView {

    fun render(state: SearchState)



}