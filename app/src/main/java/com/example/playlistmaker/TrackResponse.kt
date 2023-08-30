package com.example.playlistmaker

import com.example.playlistmaker.domain.Track

class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)