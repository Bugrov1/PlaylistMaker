package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractor (private val historyRepository: SearchHistoryRepository){

    fun read():Array<Track>?{
       return  historyRepository.read()
    }
    fun write(track: Track){
        historyRepository.write(track)
    }

    fun clear(){
        historyRepository.clear()
    }
}