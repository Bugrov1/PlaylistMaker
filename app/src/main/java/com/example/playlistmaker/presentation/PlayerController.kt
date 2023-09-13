package com.example.playlistmaker.presentation

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

class PlayerController(private val activity: Activity) {


    private var mainThreadHandler: Handler? = null
    private var mediaPlayer = Creator.providePlayerInteractor() //PlayerInteractor(PlayerImpl())
    private lateinit var play: ImageButton
    private lateinit var timer: TextView
    private lateinit var url: String

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT


    fun onCreate() {

        mainThreadHandler = Handler(Looper.getMainLooper())

        val backButton = activity.findViewById<ImageButton>(R.id.playerBack)
        val albumCover = activity.findViewById<ImageView>(R.id.albumCover)
        val trackName = activity.findViewById<TextView>(R.id.trackName)
        val artistName = activity.findViewById<TextView>(R.id.artistName)
        val trackTimeValue = activity.findViewById<TextView>(R.id.trackTimeValue)
        val albumNameValue = activity.findViewById<TextView>(R.id.albumNameValue)
        val yearValue = activity.findViewById<TextView>(R.id.yearValue)
        val genreValue = activity.findViewById<TextView>(R.id.genreValue)
        val countryValue = activity.findViewById<TextView>(R.id.countryValue)

        play = activity.findViewById(R.id.playPauseButton)
        timer = activity.findViewById(R.id.playTime)
        url = activity.intent.extras?.getString("previewUrl").toString()

        trackName.text = activity.intent.extras?.getString("trackName")
        artistName.text = activity.intent.extras?.getString("artistName")
        val formatTrackTime = activity.intent.extras?.getLong("trackTimeMillis")
        trackTimeValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(formatTrackTime)
        val roundingRadius = 10
        val albumCoverUrl =
            activity.intent.extras?.getString("artworkUrl100")
                ?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(activity.applicationContext)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(albumCover)

        albumNameValue.text = activity.intent.extras?.getString("collectionName") ?: ""
        yearValue.text = activity.intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: ""
        genreValue.text = activity.intent.extras?.getString("primaryGenreName") ?: ""
        countryValue.text = activity.intent.extras?.getString("country") ?: ""

        preparePlayer()


///Buttons actions
        backButton.setOnClickListener { activity.finish() }

        play.setOnClickListener {
            Log.v(ContentValues.TAG, "START PLAYING")
            playbackControl()

            mainThreadHandler?.post(
                createUpdateTimerTask()
            )
        }
    ///Buttons actions
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
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
            playerState = STATE_PREPARED
            timer.text = "00:00"
            play.setBackgroundResource(R.drawable.playpausebutton)

        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {

            override fun run() {
                Log.v(ContentValues.TAG, "TIMER TASK")
                when (playerState) {
                    STATE_PLAYING -> {
                        timer.text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition())
                        mainThreadHandler?.postDelayed(this, 500)

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
        play.setBackgroundResource(R.drawable.pausebutton)
        playerState = STATE_PLAYING
    }


    private fun pausePlayer() {
        mediaPlayer.pausePlayer()
        play.setBackgroundResource(R.drawable.playpausebutton)
        playerState = STATE_PAUSED
    }

}
