package com.example.playlistmaker.player.ui.viewmodel


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlayerActivityState



class PlayerViewModel(track: Track, private val mediaPlayer: PlayerInteractor) : ViewModel() {

    private val mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private var trackInit = track
    private val _state = MutableLiveData<PlayerActivityState>()
    val state: LiveData<PlayerActivityState> = _state
    init {
        trackInit.previewUrl?.let { mediaPlayer.setDataSource(it) }
        trackInit()

    }
    fun trackInit() {
        _state.postValue(PlayerActivityState.StatePlayerReady(trackInit))
        mediaPlayer.setOnCompletionListener {
            _state.postValue(PlayerActivityState.StatePlayerReady(trackInit))
            mainThreadHandler?.removeCallbacksAndMessages(null)
        }
    }
    fun playClickListen() {
        when (mediaPlayer.getState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.READY, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }
    fun playerStop() {
        mediaPlayer.stop()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.pausePlayer()
        mediaPlayer.stop()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayer.getState()) {
                    PlayerState.PLAYING -> {
                        val timerText = mediaPlayer.currentPosition()
                        mainThreadHandler?.postDelayed(this, 500)
                        _state.value = PlayerActivityState.StatePlayerPlay(timerText)
                    }

                    PlayerState.READY, PlayerState.PAUSED -> {
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.startPlayer()
        _state.postValue(mediaPlayer.let { PlayerActivityState.StatePlayerPlay(it.currentPosition()) })
    }

    fun pausePlayer() {
        mediaPlayer.pausePlayer()
        _state.postValue(PlayerActivityState.StatePlayerPause)
    }

}