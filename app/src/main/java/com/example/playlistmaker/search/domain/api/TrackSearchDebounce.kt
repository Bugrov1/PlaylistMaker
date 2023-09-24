package com.example.playlistmaker.search.domain.api

interface TrackSearchDebounce {
    fun searchDebounce(searchRequest: () -> Unit)
    fun onCleared()
}