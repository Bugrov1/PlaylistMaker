package com.example.playlistmaker.player.ui.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.State
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PlayerViewModel(
    track: Track,
    private val mediaPlayer: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private var trackInit = track
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default(trackInit.isFavorite))
    val playerState = _playerState.asStateFlow()

    init {
        trackInit.previewUrl?.let { mediaPlayer.setDataSource(it) }
        playerInit()
    }

    private var timerJob: Job? = null
    fun playerInit() {

        mediaPlayer.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared(trackInit.isFavorite)

        }
        mediaPlayer.setOnCompletionListener {
            _playerState.value = PlayerState.Prepared(trackInit.isFavorite)
            timerJob?.cancel()
            Log.v("STATUS", "Isplaying ---${mediaPlayer.isPlaying()}")
            Log.v("STATUS", "endtime ---${getCurrentPlayerPosition()}")

        }
    }

    fun onFavoriteClicked(){
        if(!trackInit.isFavorite){
            favoritesInteractor.addToFavorites(trackInit)
            trackInit.isFavorite=true
            _playerState.value.inFavorites =true
        }
        else{
            favoritesInteractor.deleteFromFavorites(trackInit)
            trackInit.isFavorite=false
            _playerState.value.inFavorites =false
        }
    }

    fun playClickListen() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
        startTimer()

    }


    override fun onCleared() {
        super.onCleared()
        releasePlayer()

    }


    private fun startTimer() {

        timerJob = viewModelScope.launch {
            while (mediaPlayer.getState() ==State.PLAYING) {
                delay(300L)
                Log.v("STATUS", "current time ---${getCurrentPlayerPosition()}")
                _playerState.value = PlayerState.Playing(getCurrentPlayerPosition(),trackInit.isFavorite)
            }


        }
    }

    private fun getCurrentPlayerPosition(): String {
        return mediaPlayer.currentPosition()

    }


    private fun startPlayer() {
        mediaPlayer.startPlayer()
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition(),trackInit.isFavorite)

    }

    fun pausePlayer() {
        mediaPlayer.pausePlayer()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition(),trackInit.isFavorite)

    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        timerJob?.cancel()
        _playerState.value = PlayerState.Default(trackInit.isFavorite)
    }

    fun playerStop() {
        mediaPlayer.stop()
        timerJob?.cancel()
    }

}