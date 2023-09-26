package com.example.playlistmaker.search.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.ui.models.SearchState

import com.example.playlistmaker.util.Creator


class SearchViewModel : ViewModel() {
    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel()
            }
        }
    }

    private val searchHistoryProvider = Creator.provideSearchHistory()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


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
        renderState(SearchState.Update(searchHistoryProvider.read()))

    }

    fun clear() {
        searchHistoryProvider.clear()

    }


    private val trackSearchDebounce = Creator.getSearchDebounce()
    fun searchDebounce2(changedText: String) {

        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText

        trackSearchDebounce.searchDebounce { searchRequest(changedText) }

    }

    private var lastSearchText: String? = null

    private val tracksInteractor = Creator.provideTrackInteractor()

    override fun onCleared() {
        super.onCleared()
        trackSearchDebounce.onCleared()

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
                                    "Проблемы со связью Загрузка не удалась. Проверьте подключение к интернету"
//                                    getApplication<Application>().getString(R.string.something_went_wrong)
                                renderState(SearchState.Error(message))

                            }

                            trackList.isEmpty() -> {
                                val message = "Ничего не нашлось"
//                                    getApplication<Application>().getString(R.string.nothing_found)
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