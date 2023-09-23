package com.example.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.main.domain.model.ScreenState
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.util.Creator

class MainViewModel: ViewModel() {

    private val _screenState = SingleLiveEvent<ScreenState>()
    //private val _screenState = MutableLiveData<ScreenState>()

    val screenState: LiveData<ScreenState> = _screenState
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {MainViewModel()
            }
        }
    }

    fun onSeachButtonClicked(){
        _screenState.value = ScreenState.Search
    }

    fun onMediaButtonClicked(){
        _screenState.value = ScreenState.Mediateka
    }

    fun onSettingsButtonClicked(){
        _screenState.value = ScreenState.Settings
    }


}
