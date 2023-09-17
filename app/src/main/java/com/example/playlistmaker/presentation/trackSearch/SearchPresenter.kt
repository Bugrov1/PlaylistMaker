package com.example.playlistmaker.presentation.trackSearch

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import com.example.playlistmaker.R

import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.ui.tracks.models.SearchState

import com.example.playlistmaker.util.Creator

class SearchPresenter(
    private val view: SearchView,
    private val context: Context,

) {
    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchHistoryProvider = Creator.provideSearchHistory()

    fun read(): Array<Track>? {
        return searchHistoryProvider.read()
    }
    fun write(track:Track){
        searchHistoryProvider.write(track)
    }

    fun clear(){
        searchHistoryProvider.clear()
    }

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val tracksInteractor = Creator.provideTrackInteractor(context)
    private val trackList = arrayListOf<Track>()



    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText) }

    fun searchDebounce(changedText: String) {
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            view.render(
                SearchState.Loading
            )

            tracksInteractor.searchTracks(
                newSearchText,
                object : TrackInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            if (foundTracks != null) {
                                trackList.clear()
                                trackList.addAll(foundTracks)
//
                            }
                            when{
                                errorMessage != null -> {
                                    val message = context.getString(R.string.something_went_wrong)
                                    view.render(SearchState.Error(message))

                                }
                                trackList.isEmpty() -> {
                                    val message =context.getString(R.string.nothing_found)
                                    view.render(SearchState.Empty(message))

                                }
                                else -> {
                                    //view.showContent(trackList)
                                    view.render(SearchState.Content(trackList))
                                }
                            }

                        }
                    }
                })
        }
    }


    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }


}