package com.example.playlistmaker.player.ui.activity

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val roundingRadius = 10
    private lateinit var backButton: ImageButton
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
    private lateinit var track: Track
    private lateinit var likebutton: ImageButton

    val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        track = Gson().fromJson((intent.getStringExtra("track")), Track::class.java)
        initViews()
        setListeners()

        lifecycleScope.launchWhenStarted {
            viewModel.playerState.collect {
                play.isEnabled = it.isPlayButtonEnabled
                buttonStatus(it.buttonText)
                timer.text = it.progress
                renderFavoritebutton(it.inFavorites)
                Log.v(ContentValues.TAG, "$it")
                Log.v(ContentValues.TAG, "timer ui ---${it.progress}")
            }
        }

//        viewModel.observePlayerState().observe(this) {
//            play.isEnabled = it.isPlayButtonEnabled
//            buttonStatus(it.buttonText)
//            timer.text = it.progress
//            Log.v(ContentValues.TAG, "$it")
//            Log.v(ContentValues.TAG, "timer ui ---${it.progress}")
//        }
    }

    fun setListeners() {
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            viewModel.playerStop()
        }

        play.setOnClickListener {
            viewModel.playClickListen()
        }
    }

    private fun initViews() {
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
        likebutton = findViewById(R.id.likeButton)

        setupDetails(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.onDestroy()
    }

    private fun setupDetails(
        track: Track
    ) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        albumNameValue.text = track.collectionName ?: ""
        yearValue.text = track.releaseDate?.subSequence(0, 4) ?: ""
        genreValue.text = track.primaryGenreName ?: ""
        countryValue.text = track.country ?: ""

        val albumCoverUrl =
            track.artworkUrl100
                .replaceAfterLast('/', "512x512bb.jpg")//done
        println(albumCoverUrl)

        Glide.with(this)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(albumCover)
    }


    private fun buttonStatus(buttonStatus: String) {
        when (buttonStatus) {
            "PAUSE" -> play.setBackgroundResource(R.drawable.pausebutton)
            "PLAY" -> play.setBackgroundResource(R.drawable.playpausebutton)


        }
    }
    private fun renderFavoritebutton(inFavorites:Boolean){
        if(true){
            likebutton.setBackgroundResource(R.drawable.likebuttontrue)
        }

        else{
            likebutton.setBackgroundResource(R.drawable.likebutton)
        }
    }

}
