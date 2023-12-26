package com.example.playlistmaker.mediateka.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.ButtonState
import com.example.playlistmaker.mediateka.ui.models.FavoritesState
import kotlinx.coroutines.launch

open class CreatePlaylistViemodel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val _stateLiveData = MutableLiveData<ButtonState>()
    val state: LiveData<ButtonState> = _stateLiveData

    init{
        _stateLiveData.postValue(ButtonState.Disabled)
    }

    fun changeButtonstate(s:String){
        if (!s.isEmpty()){

            _stateLiveData.postValue(ButtonState.Enabled)
        }
        else{_stateLiveData.postValue(ButtonState.Disabled)  }
    }

    open fun createPlaylist(playlist: Playlist){
        viewModelScope.launch{ playlistInteractor.insertList(playlist)}

    }

}