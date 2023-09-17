package com.example.playlistmaker.ui.player

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
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.player.PlayerController
import com.example.playlistmaker.presentation.player.PlayerView

import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), PlayerView {


    private lateinit var playerController: PlayerController
    private lateinit var play: ImageButton
    private lateinit var timer: TextView
    private lateinit var url: String
    private val roundingRadius = 10
    private lateinit var albumCover: ImageView

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTimeValue: TextView
    private lateinit var albumNameValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        val backButton = findViewById<ImageButton>(R.id.playerBack)
        albumCover = findViewById<ImageView>(R.id.albumCover)
        trackName = findViewById<TextView>(R.id.trackName)
        artistName = findViewById<TextView>(R.id.artistName)
        trackTimeValue = findViewById<TextView>(R.id.trackTimeValue)
        albumNameValue = findViewById<TextView>(R.id.albumNameValue)
        yearValue = findViewById<TextView>(R.id.yearValue)
        genreValue = findViewById<TextView>(R.id.genreValue)
        countryValue = findViewById<TextView>(R.id.countryValue)

        play = findViewById(R.id.playPauseButton)
        timer = findViewById(R.id.playTime)

        url = intent.extras?.getString("previewUrl").toString()//done

        //trackName.text = intent.extras?.getString("trackName")
        val trackNameText = intent.extras?.getString("trackName")
        //artistName.text = intent.extras?.getString("artistName")
        val artistNameText = intent.extras?.getString("artistName")
        val formatTrackTime = intent.extras?.getLong("trackTimeMillis")
        //trackTimeValue.text =
        //     SimpleDateFormat("mm:ss", Locale.getDefault()).format(formatTrackTime)
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(formatTrackTime)
        val roundingRadius = 10
        val albumCoverUrl =
            intent.extras?.getString("artworkUrl100")
                ?.replaceAfterLast('/', "512x512bb.jpg")//done

//        Glide.with(applicationContext)
//            .load(albumCoverUrl)
//            .placeholder(R.drawable.placeholderbig)
//            .centerCrop()
//            .transform(RoundedCorners(roundingRadius))
//            .into(albumCover)

        // albumNameValue.text = intent.extras?.getString("collectionName") ?: ""
        val albumNameValueText = intent.extras?.getString("collectionName") ?: ""
        // yearValue.text = intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: ""
        val yearValueText = intent.extras?.getString("releaseDate")?.subSequence(0, 4) ?: ""
        // genreValue.text = intent.extras?.getString("primaryGenreName") ?: ""
        val genreValueText = intent.extras?.getString("primaryGenreName") ?: ""
        // countryValue.text = intent.extras?.getString("country") ?: ""
        val countryValueText = intent.extras?.getString("country") ?: ""

        playerController = Creator.providePlayerController(
            this, albumCoverUrl = albumCoverUrl, url = url,
            trackNameText = trackNameText,
            artistNameText = artistNameText,
            trackTime = trackTime,
            albumNameValueText = albumNameValueText,
            yearValueText = yearValueText,
            genreValueText = genreValueText,
            countryValueText = countryValueText
        )
        playerController.onCreate()


        backButton.setOnClickListener { finish() }

        play.setOnClickListener {
            playerController.playClickListen()
        }


    }

    override fun onPause() {
        super.onPause()
        playerController.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.onDestroy()
    }

    override fun setupTextValues(
        trackNameText: String?,
        artistNameText: String?,
        trackTime: String,
        albumNameValueText: String,
        yearValueText: CharSequence,
        genreValueText: String,
        countryValueText: String
    ) {
        trackName.text = trackNameText
        artistName.text = artistNameText
        trackTimeValue.text = trackTime
        albumNameValue.text = albumNameValueText
        yearValue.text = yearValueText
        genreValue.text = genreValueText
        countryValue.text = countryValueText
    }

    override fun setTimerText(text: String) {
        timer.text = text
    }

    override fun setupAlbumCover(albumCoverUrl: String?) {
        Glide.with(applicationContext)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(albumCover)
    }

    override fun setPlayButtonStatus(pressed: Boolean) {
        if (pressed) {
            play.setBackgroundResource(R.drawable.pausebutton)
        } else {
            play.isEnabled = true
            play.setBackgroundResource(R.drawable.playpausebutton)
        }
    }


}
