package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.Track

interface SearchHistoryRepository {

    fun read():Array<Track>?

    fun write(track: Track)

    fun clear()

}