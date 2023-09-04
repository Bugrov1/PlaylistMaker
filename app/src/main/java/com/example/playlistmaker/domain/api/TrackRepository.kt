package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>
}