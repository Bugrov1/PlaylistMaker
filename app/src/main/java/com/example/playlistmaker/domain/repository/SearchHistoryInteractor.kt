package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.Track

class SearchHistoryInteractor (private val historyRepository:SearchHistoryRepository){

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