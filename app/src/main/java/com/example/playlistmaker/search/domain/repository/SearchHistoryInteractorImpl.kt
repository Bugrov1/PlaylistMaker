package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl (private val historyRepository: SearchHistoryRepository):SearchHistoryInteractor{

    override fun read(): Array<Track>? {
        return historyRepository.read()
    }
    override fun write(track: Track){
        historyRepository.write(track)
    }

    override fun clear(){
        historyRepository.clear()
    }
}