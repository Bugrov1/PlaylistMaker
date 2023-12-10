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

class CreatePlaylistViemodel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<ButtonState>()
    fun observeButtonState(): LiveData<ButtonState> = stateLiveData

    init{
        stateLiveData.postValue(ButtonState.Disabled)
    }

    fun changeButtonstate(s:String){
        if (!s.isEmpty()){

            stateLiveData.postValue(ButtonState.Enabled)
        }
        else{stateLiveData.postValue(ButtonState.Disabled)  }
    }

    fun createPlaylist(playlist: Playlist){
        viewModelScope.launch{ playlistInteractor.insertList(playlist)}

    }

}