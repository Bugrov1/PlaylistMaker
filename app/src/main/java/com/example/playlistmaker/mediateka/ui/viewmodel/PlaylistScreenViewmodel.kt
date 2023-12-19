package com.example.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistScreenViewmodel(id:String,
                              private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private var idInit = id.toLong()


    private val _stateLiveData = MutableLiveData<Playlist>()
    val state: LiveData<Playlist> = _stateLiveData
    init{
        fillData(idInit)

    }

    private fun fillData(id:Long){
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(id)
            render(playlist )
        }
    }

    private fun render(playlist: Playlist) {
        _stateLiveData.postValue(playlist)
    }





}