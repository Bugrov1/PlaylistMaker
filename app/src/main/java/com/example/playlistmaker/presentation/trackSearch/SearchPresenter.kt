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

            view.showProgressBar(true)
            view.showPlaceholderMessage(false)
            view.showRecyclerView(false)

            tracksInteractor.searchTracks(
                newSearchText,
                object : TrackInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            if (foundTracks != null) {
                                view.showProgressBar(false)
                                trackList.clear()
                                trackList.addAll(foundTracks)
                                view.updateTracksList(trackList)
                                view.showRecyclerView(true)

                            }
                            if (errorMessage != null) {
                                showMessage(false)
                            } else if (trackList.isEmpty()) {
                                //Log.v(ContentValues.TAG, "Something here")
                                showMessage(true)
                            }
                        }
                    }
                })
        }
    }

    private fun showMessage(noDataFound: Boolean) {
        if (noDataFound) {
            view.showProgressBar(false)
            view.setPlaceholderImage(image = R.drawable.nothing_found_image)
            view.changePlaceholderText(R.string.nothing_found)
            view.showPlaceholderImage(true)
            view.showPlaceholderMessage(true)
            view.showPlaceholderButton(false)
            view.showHistoryView(false)
            trackList.clear()
            view.updateTracksList(trackList)


        } else {
            view.showProgressBar(false)
            view.setPlaceholderImage(image = R.drawable.goes_wrong_image)
            view.changePlaceholderText(R.string.something_went_wrong)
            view.showPlaceholderImage(true)
            view.showPlaceholderMessage(true)
            view.showPlaceholderButton(true)
            view.showHistoryView(false)
            trackList.clear()
            view.updateTracksList(trackList)
        }
    }


    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }


}