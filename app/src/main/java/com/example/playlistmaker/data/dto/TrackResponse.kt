package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.Track

class TrackResponse(
    val resultCount: Int,
    val results: List<Track>//поменять на TrackDTO когда смогу реализовать поиск
):Response()