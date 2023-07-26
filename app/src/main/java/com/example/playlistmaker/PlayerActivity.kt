package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val backButton = findViewById<ImageButton>(R.id.playerBack)
        val albumCover = findViewById<ImageView>(R.id.albumCover)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTimeValue = findViewById<TextView>(R.id.trackTimeValue)
        val albumNameValue = findViewById<TextView>(R.id.albumNameValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val genreValue = findViewById<TextView>(R.id.genreValue)
        val countryValue = findViewById<TextView>(R.id.countryValue)

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


    }


}


