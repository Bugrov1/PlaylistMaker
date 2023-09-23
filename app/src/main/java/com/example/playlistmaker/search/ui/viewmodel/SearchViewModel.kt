package com.example.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.playlistmaker.R

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.ui.models.SearchState

import com.example.playlistmaker.util.Creator
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()

        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
    private var history= emptyArray<Track>()
    private val searchHistoryProvider = Creator.provideSearchHistory()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val _historyData = MutableLiveData<Array<Track>?>()
    val historyData: LiveData<Array<Track>?> = _historyData

    init {
        history = searchHistoryProvider.read()?:emptyArray<Track>()
        renderState(SearchState.History(searchHistoryProvider.read()))
        _historyData.value = history
    }





    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun historyload() {
        renderState(SearchState.History(searchHistoryProvider.read()))
        _historyData.value = searchHistoryProvider.read()
    }



    fun write(track: Track) {
        searchHistoryProvider.write(track)
        _historyData.value = searchHistoryProvider.read()

    }

    fun clear() {
        searchHistoryProvider.clear()
        _historyData.value = searchHistoryProvider.read()
        Log.v("View", "History cleared")
        // Log.v("View","History read")
    }

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val tracksInteractor = Creator.provideTrackInteractor(getApplication<Application>())

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }

        this.lastSearchText = changedText

        val searchRunnable = Runnable { searchRequest(changedText) }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )


    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val trackList = mutableListOf<Track>()
                        if (foundTracks != null) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                val message =
                                    getApplication<Application>().getString(R.string.something_went_wrong)
                                renderState(SearchState.Error(message))

                            }

                            trackList.isEmpty() -> {
                                val message =
                                    getApplication<Application>().getString(R.string.nothing_found)
                                renderState(SearchState.Empty(message))

                            }

                            else -> {
                                renderState(SearchState.Content(trackList))
                            }
                        }


                    }
                })
        }
    }


}