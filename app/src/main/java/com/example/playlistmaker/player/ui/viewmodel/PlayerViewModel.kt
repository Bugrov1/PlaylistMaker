package com.example.playlistmaker.player.ui.viewmodel

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlayerActivityState

import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(track: Track) : ViewModel() {

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private var mediaPlayer = Creator.providePlayerInteractor()


    private var trackInit = track



    private val _track= MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _state = MutableLiveData<PlayerActivityState>()
    val state: LiveData<PlayerActivityState> = _state

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> = _timer

    init {
        Log.d("TEST", "")
        trackInit()
    }

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                // 1
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(
                        track = track
                    ) as T
                }
            }
    }




    fun trackInit(){
        _track.postValue(trackInit)
        preparePlayer(trackInit.previewUrl!!)
    }

    fun playClickListen() {
        Log.v(ContentValues.TAG, "START PLAYING")
        playbackControl()
        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    fun onPause() {
        pausePlayer()
    }

    fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }

    override fun onCleared() {
        super.onCleared()
       mediaPlayer.pausePlayer()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
  }

    private fun preparePlayer(url:String) {
        mediaPlayer.preparePlayer(url = url)
        mediaPlayer.setOnPreparedListener {
            _state.postValue(PlayerActivityState.StatePlayerReady)
        }
        mediaPlayer.setOnCompletionListener {
            _state.postValue(PlayerActivityState.StatePlayerReady)
            //mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
            mainThreadHandler?.removeCallbacksAndMessages(null)




        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {

            override fun run() {
                Log.v(ContentValues.TAG, "TIMER TASK")
                when (mediaPlayer.getState()) {
                    PlayerState.PLAYING -> {
                        val timerText = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition())
                        mainThreadHandler?.postDelayed(this, 500)
                        _timer.value = timerText

                    }

                    PlayerState.READY, PlayerState.PAUSED -> {


                        mainThreadHandler?.removeCallbacks(this)
                    }

                    else -> {}
                }
                Log.v(ContentValues.TAG, "Time is $mediaPlayer.currentPosition")
            }
        }
    }

    private fun playbackControl() {
        when (mediaPlayer.getState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.READY, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun startPlayer() {
        mediaPlayer.startPlayer()

        _state.postValue(PlayerActivityState.StatePlayerPlay)
    }


    fun pausePlayer() {
        mediaPlayer.pausePlayer()

        _state.postValue(PlayerActivityState.StatePlayerPause)
    }

}







