package com.example.playlistmaker.mediateka.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistScreenViewmodel(id:String,
                              private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private var idInit = id.toLong()


    private val _stateLiveData = MutableLiveData<Playlist>()
    val state: LiveData<Playlist> = _stateLiveData

    private val _durationLiveData = MutableLiveData<String>()
    val duration: LiveData<String> = _durationLiveData

    private val _tracksLiveData = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracksLiveData
    init{
        fillData(idInit)

    }

    private fun fillData(id:Long) {

        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(id)
            render(playlist)
            val tracks = Gson().fromJson(playlist.tracks,Array<Int>::class.java)?: emptyArray()
            playlistInteractor.getracks(tracks.toMutableList())
                .collect { tracklist->
                    processResult(tracklist)
                    _tracksLiveData.postValue(tracklist)
                }
        }

    }

        private fun render(playlist: Playlist) {
            _stateLiveData.postValue(playlist)
        }

    private fun processResult(tracks: List<Track>) {
        Log.v("TRACKSFOUND", "$tracks")
        var durationSum = 0.0
        for(track in tracks){
            durationSum+=track.trackTimeMillis
        }
        val durationSumConverted = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
        _durationLiveData.postValue(durationSumConverted )

    }







}