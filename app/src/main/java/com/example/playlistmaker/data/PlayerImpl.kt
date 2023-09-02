package com.example.playlistmaker.data

import android.content.ContentValues
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Player
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerImpl():Player {
    var playerState = PlayerStatus.STATE_DEFAULT
    var mediaPlayer = MediaPlayer()

    override fun preparePlayer(play:ImageButton, url:String,  timer:TextView,  mainThreadHandler: Handler?) {

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = PlayerStatus.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(createUpdateTimerTask(timer,mainThreadHandler))
            playerState = PlayerStatus.STATE_PREPARED
            timer.text ="00:00"
            play.setBackgroundResource(R.drawable.playpausebutton)

        }
    }

    override fun startPlayer( play:ImageButton) {

        mediaPlayer.start()
        play.setBackgroundResource(R.drawable.pausebutton)
        playerState = PlayerStatus.STATE_PLAYING
    }

    override fun pausePlayer(play:ImageButton) {
        mediaPlayer.pause()
        play.setBackgroundResource(R.drawable.playpausebutton)
        playerState = PlayerStatus.STATE_PAUSED

    }

    override fun playbackControl( play:ImageButton) {
        when(playerState) {
            PlayerStatus.STATE_PLAYING -> {
                pausePlayer( play)
                Log.v(ContentValues.TAG, "Pause")
            }
            PlayerStatus.STATE_PREPARED, PlayerStatus.STATE_PAUSED -> {
                startPlayer( play)
                Log.v(ContentValues.TAG, "play")
            }

            else -> {}
        }
    }

     fun createUpdateTimerTask(timer: TextView, mainThreadHandler:Handler): Runnable {
        return object : Runnable {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun run() {
                Log.v(ContentValues.TAG, "TIMER TASK")
                when (playerState) {
                    PlayerStatus.STATE_PLAYING -> {
                        timer.text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.postDelayed(this, 500)

                    }

                    PlayerStatus.STATE_PREPARED, PlayerStatus.STATE_PAUSED -> {

                        //timer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                        mainThreadHandler?.removeCallbacks(this)
                    }

                    else -> {}
                }
                Log.v(ContentValues.TAG, "Time is $mediaPlayer.currentPosition")
            }
        }
    }
}