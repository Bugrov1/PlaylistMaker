package com.example.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.ui.models.FavoritesState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    init {
        fillData()

    }


    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor.getTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(movies: List<Track>) {
        if (movies.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(movies))
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }

    fun refresh(){
        fillData()
    }
}





