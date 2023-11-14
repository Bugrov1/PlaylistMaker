package com.example.playlistmaker.player.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class PlayerViewModel(track: Track,private val mediaPlayer: PlayerInteractor) : ViewModel() {

    private var trackInit = track
    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        trackInit.previewUrl?.let { mediaPlayer.setDataSource(it) }
        playerInit()
    }

    private var timerJob: Job? = null
    fun playerInit() {

        mediaPlayer.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared())

        }
        mediaPlayer.setOnCompletionListener {
            playerState.postValue(PlayerState.Prepared())
            timerJob?.cancel()


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
            while (mediaPlayer.isPlaying() ) {
                delay(300L)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return mediaPlayer.currentPosition()

    }


    private fun startPlayer() {
        mediaPlayer.startPlayer()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))

    }

    fun pausePlayer() {
        mediaPlayer.pausePlayer()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))

    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        timerJob?.cancel()
        playerState.value = PlayerState.Default()
    }

    fun playerStop() {
        mediaPlayer.stop()
        timerJob?.cancel()
    }

}