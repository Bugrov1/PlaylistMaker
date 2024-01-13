package com.example.playlistmaker.search.ui.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel(
    val searchHistoryProvider: SearchHistoryInteractor,
    private val tracksInteractor: TrackInteractor,
    private val favoritesInteractor: FavoritesInteractor

) : ViewModel() {


    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
    }

    init {
        checkFavorites()
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun historyload() {
        checkFavorites()
    }

    fun checkFavorites() {
        val history = searchHistoryProvider.read()
        viewModelScope.launch {
            val ids = favoritesInteractor.getIds()
            if (history != null) {
                val checked =
                    history.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            isFavorite = it.trackId in ids
                        )
                    }
                renderState(SearchState.History(checked.toTypedArray()))
            } else {
                renderState(SearchState.History(emptyArray()))
            }
        }
    }

    fun write(track: Track) {
        searchHistoryProvider.write(track)
    }

    fun read(): Array<Track>? {
        return searchHistoryProvider.read()
    }

    fun update() {
        renderState(SearchState.History(searchHistoryProvider.read()))
    }

    fun clear() {

        searchHistoryProvider.clear()
        renderState(SearchState.History(searchHistoryProvider.read()))

    }

    private var searchJob: Job? = null
    var lastSearchText: String? = null
    fun searchDebounce2(changedText: String) {
        if(changedText.isEmpty()){
            update()
        }
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        searchJob?.cancel()


        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    fun onPlaceHolderButtonClicked(text: String) {
        if (text.isNotEmpty()){  viewModelScope.launch {
            searchRequest(text)
        }
        }else{update()}//добавил апдейт

    }


    fun refresh() {
        Log.v("NAV", "resfreshed")
        when (stateLiveData.value) {
            is SearchState.History -> historyload()
            is SearchState.Content -> viewModelScope.launch {
                lastSearchText?.let { searchRequest(it) }
            }

            else -> {}
        }


    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)

                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        Log.v("SEARCH", "income data $errorMessage")
        val trackList = mutableListOf<Track>()
        if (foundTracks != null) {
            trackList.clear()
            trackList.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                if (errorMessage == "Ошибка сервера") {
                    val message =
                        "Ошибка сервера"
                    renderState(SearchState.Error(message))
                } else {
                    val message =
                        "Проблемы со связью Загрузка не удалась. Проверьте подключение к интернету"
                    renderState(SearchState.Error(message))
                }

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

}