package com.example.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EditPlaylistViewModel(id:String,private val playlistInteractor: PlaylistInteractor) : CreatePlaylistViemodel(
    playlistInteractor
) {
    private var idInit = id.toLong()

    private val _stateLiveData = MutableLiveData<Playlist>()
    val data: LiveData<Playlist> = _stateLiveData

    init{
        fillData(idInit)

    }

    private fun fillData(id:Long) {

        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(id)
            render(playlist)

        }

    }

    private fun render(playlist: Playlist) {
        _stateLiveData.postValue(playlist)
    }


   fun savePlaylist(playlist: Playlist){
       viewModelScope.launch {playlistInteractor.updateList(playlist)}
   }

}