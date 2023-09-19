package com.example.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerView
import com.example.playlistmaker.ui.tracks.models.SearchState
import com.google.gson.Gson

import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {



    private val roundingRadius = 10
   private lateinit var backButton:ImageButton
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTimeValue: TextView
    private lateinit var albumNameValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var play: ImageButton
    private lateinit var timer: TextView
    private lateinit var url: String

    private lateinit var viewModel: PlayerViewModel




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        val track: Track = Gson().fromJson((intent.getStringExtra("track")), Track::class.java)
        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory( track=track))[PlayerViewModel::class.java]

        setupDetails(track)
        preparePlayer(url=track.previewUrl!!)
       viewModel.timer.observe(this){
            setTimerText(it)
       }
        viewModel.state.observe(this){         render(it)
       }

        backButton.setOnClickListener { finish()
            viewModel.onPause()
        }

        play.setOnClickListener {
            viewModel.playClickListen()
        }


    }

    fun initViews(){

        backButton = findViewById(R.id.playerBack)
        albumCover = findViewById(R.id.albumCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTimeValue = findViewById(R.id.trackTimeValue)
        albumNameValue = findViewById(R.id.albumNameValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        play = findViewById(R.id.playPauseButton)
        timer = findViewById(R.id.playTime)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
    fun setupDetails(
        track:Track
    ) {
        trackName.text = track?.trackName
        artistName.text = track?.artistName
        trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        albumNameValue.text = track?.collectionName?: ""
        yearValue.text = track?.releaseDate?.subSequence(0, 4) ?: ""
        genreValue.text = track?.primaryGenreName?: ""
        countryValue.text = track?.country?: ""


        val albumCoverUrl =
            track?.artworkUrl100
                ?.replaceAfterLast('/', "512x512bb.jpg")//done

        Glide.with(this)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(albumCover)
    }

    fun setTimerText(text: String) {
        timer.text = text
    }


     fun setPlayButtonStatus(playing: Boolean) {
        if (playing) {
            play.setBackgroundResource(R.drawable.pausebutton)
        } else {
            play.isEnabled = true
            play.setBackgroundResource(R.drawable.playpausebutton)
        }
    }

    fun preparePlayer(url:String){
        viewModel.preparePlayer(url)
        timer.text = "00:00"
        play.setBackgroundResource(R.drawable.playpausebutton)
        play.isEnabled = true

    }

    fun render(state: PlayerState) {
        when (state) {
            //PlayerState.READY -> preparePlayer(track.previewUrl!!)
            PlayerState.PLAYING -> setPlayButtonStatus(true)
            PlayerState.PAUSED -> setPlayButtonStatus(false)

            else -> {}
        }
    }





}
