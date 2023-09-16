package com.example.playlistmaker.presentation.trackSearch

import com.example.playlistmaker.domain.Track

interface SearchView {

    fun showPlaceholderMessage(isVisible: Boolean)
    fun  showPlaceholderButton(isVisible: Boolean)
    fun  showPlaceholderImage(isVisible: Boolean)
    fun showProgressBar(isVisible: Boolean)
    fun showHistoryView(isVisible: Boolean)
    fun showRecyclerView(isVisible: Boolean)

    fun changePlaceholderText(newMessage:Int)

    fun setPlaceholderImage(image:Int)

    fun updateTracksList(newTrackList: List<Track>)



}