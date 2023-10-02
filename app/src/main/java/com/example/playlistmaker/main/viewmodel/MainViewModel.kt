package com.example.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.domain.model.ScreenState

class MainViewModel : ViewModel() {

    private val _screenState = NavigationEvents<ScreenState>()

    val screenState: LiveData<ScreenState> = _screenState


    fun onSeachButtonClicked() {
        _screenState.value = ScreenState.Search
    }

    fun onMediaButtonClicked() {
        _screenState.value = ScreenState.Mediateka
    }

    fun onSettingsButtonClicked() {
        _screenState.value = ScreenState.Settings
    }

}
