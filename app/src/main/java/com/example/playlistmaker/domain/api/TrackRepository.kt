package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.util.Resource

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}