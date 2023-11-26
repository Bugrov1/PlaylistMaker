package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun read():Array<Track>?
     fun write(track: Track)
     fun clear()

}