package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): TrackResponse
//    suspend fun doRequest2(dto: Any): Response
}