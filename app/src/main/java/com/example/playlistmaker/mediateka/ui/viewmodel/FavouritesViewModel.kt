package com.example.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.ui.models.FavoritesState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _stateLiveData = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _stateLiveData

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

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        _stateLiveData.postValue(state)
    }

    fun refresh() {
        fillData()
    }
}





