package com.example.playlistmaker.presentation.player

import android.app.Activity
import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerController(
    private val view: PlayerView,
    private val albumCoverUrl: String?,
    private val url: String,
    private val trackNameText: String?,
    private val artistNameText: String?,
    private val trackTime: String,
    private val albumNameValueText: String,
    private val yearValueText: CharSequence,
    private val genreValueText: String,
    private val countryValueText: String

) {


    private var mainThreadHandler: Handler? = null
    private var mediaPlayer = Creator.providePlayerInteractor() //PlayerInteractor(PlayerImpl())


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT


    fun onCreate() {

        mainThreadHandler = Handler(Looper.getMainLooper())

        view.setupAlbumCover(albumCoverUrl)
        view.setupTextValues(
            trackNameText = trackNameText,
            artistNameText = artistNameText,
            trackTime = trackTime,
            albumNameValueText = albumNameValueText,
            yearValueText = yearValueText,
            genreValueText = genreValueText,
            countryValueText = countryValueText
        )
        preparePlayer()


///Buttons actions

//        play.setOnClickListener {
//            Log.v(ContentValues.TAG, "START PLAYING")
//            playbackControl()
//
//            mainThreadHandler?.post(
//                createUpdateTimerTask()
//            )
//        }
        ///Buttons actions
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


    ///Functions
    private fun preparePlayer() {
        mediaPlayer.preparePlayer(url = url)
        mediaPlayer.setOnPreparedListener {
            //play.isEnabled = true
            view.setPlayButtonStatus(false)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
            playerState = STATE_PREPARED
            //timer.text = "00:00"
            view.setTimerText("00:00")
            //play.setBackgroundResource(R.drawable.playpausebutton)
            view.setPlayButtonStatus(false)

        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {

            override fun run() {
                Log.v(ContentValues.TAG, "TIMER TASK")
                when (playerState) {
                    STATE_PLAYING -> {
                        val timerText = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition())
                        mainThreadHandler?.postDelayed(this, 500)

                        view.setTimerText(timerText)

                    }

                    STATE_PREPARED, STATE_PAUSED -> {


                        mainThreadHandler?.removeCallbacks(this)
                    }
                }
                Log.v(ContentValues.TAG, "Time is $mediaPlayer.currentPosition")
            }
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.startPlayer()
       //play.setBackgroundResource(R.drawable.pausebutton)
        view.setPlayButtonStatus(true)
        playerState = STATE_PLAYING
    }


    private fun pausePlayer() {
        mediaPlayer.pausePlayer()
        //play.setBackgroundResource(R.drawable.playpausebutton)
        view.setPlayButtonStatus(false)
        playerState = STATE_PAUSED
    }

}
