package com.example.playlistmaker.search.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel(val searchHistoryProvider:SearchHistoryInteractor,
                      private val tracksInteractor:TrackInteractor) : ViewModel() {


    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    init {
        renderState(SearchState.History(searchHistoryProvider.read()))
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun historyload() {
        renderState(SearchState.History(searchHistoryProvider.read()))
    }

    fun write(track: Track) {
        searchHistoryProvider.write(track)

    }

    fun update(){
        renderState(SearchState.History(searchHistoryProvider.read()))
    }

    fun clear() {
        searchHistoryProvider.clear()
    }
    private var searchJob: Job? = null

    private var lastSearchText: String? = null
    fun searchDebounce2(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        searchJob?.cancel()
//        trackSearchDebounce.searchDebounce { searchRequest(changedText) }

        searchJob =viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(
                newSearchText,
                object : TrackInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val trackList = mutableListOf<Track>()
                        if (foundTracks != null) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                val message =
                                    "Проблемы со связью Загрузка не удалась. Проверьте подключение к интернету"
                                renderState(SearchState.Error(message))
                            }

                            trackList.isEmpty() -> {
                                val message = "Ничего не нашлось"
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