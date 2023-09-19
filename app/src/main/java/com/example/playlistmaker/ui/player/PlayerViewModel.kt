package com.example.playlistmaker.ui.player

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.ui.tracks.models.SearchState
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(track: Track) : ViewModel() {

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private var mediaPlayer = Creator.providePlayerInteractor()

    init {
        Log.d("TEST", "")
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


    private val _state = MutableLiveData<PlayerState>()
    val state: LiveData<PlayerState> = _state

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> = _timer


    //preparePlayer()


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


    ///Functions
    fun preparePlayer(url: String) {
        mediaPlayer.preparePlayer(url = url)
        mediaPlayer.setOnPreparedListener {
            //play.isEnabled = true
            _state.postValue(PlayerState.READY)
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
            _state.postValue(PlayerState.READY)
            //timer.text = "00:00"
            //play.setBackgroundResource(R.drawable.playpausebutton)


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
                        _timer.postValue(timerText)

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
        //play.setBackgroundResource(R.drawable.pausebutton)
        //view.setPlayButtonStatus(true)
        _state.postValue(PlayerState.PLAYING)
    }


    fun pausePlayer() {
        mediaPlayer.pausePlayer()
        //play.setBackgroundResource(R.drawable.playpausebutton)
        //view.setPlayButtonStatus(false)
        _state.postValue(PlayerState.PAUSED)
    }

}







