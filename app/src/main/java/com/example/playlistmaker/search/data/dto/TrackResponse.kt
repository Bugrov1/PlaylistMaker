package com.example.playlistmaker.search.data.dto

class TrackResponse(
    var code: Int = 0,
    val results: List<TrackDto>
): Response()