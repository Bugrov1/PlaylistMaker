package com.example.playlistmaker.ui

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R

import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {



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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mainThreadHandler = Handler(Looper.getMainLooper())

        val backButton = findViewById<ImageButton>(R.id.playerBack)
        val albumCover = findViewById<ImageView>(R.id.albumCover)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTimeValue = findViewById<TextView>(R.id.trackTimeValue)
        val albumNameValue = findViewById<TextView>(R.id.albumNameValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val countryValue = findViewById<TextView>(R.id.countryValue)
        play = findViewById(R.id.playPauseButton)
        timer = findViewById(R.id.playTime)
        url = intent.extras?.getString("previewUrl").toString()


        backButton.setOnClickListener { finish() }

        trackName.text = intent.extras?.getString("trackName")
        artistName.text = intent.extras?.getString("artistName")
        val formatTrackTime = intent.extras?.getLong("trackTimeMillis")
        trackTimeValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(formatTrackTime)
        val roundingRadius = 10
        val albumCoverUrl =
            intent.extras?.getString("artworkUrl100")?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(applicationContext)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(albumCover)
        albumNameValue.text = intent.extras?.getString("collectionName") ?: ""
        yearValue.text = intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: ""
        genreValue.text = intent.extras?.getString("primaryGenreName") ?: ""
        countryValue.text = intent.extras?.getString("country") ?: ""

        preparePlayer()
        play.setOnClickListener {
            Log.v(ContentValues.TAG, "START PLAYING")
            playbackControl()

            mainThreadHandler?.post(
                createUpdateTimerTask()
            )
        }

    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(createUpdateTimerTask())
    }


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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
}
