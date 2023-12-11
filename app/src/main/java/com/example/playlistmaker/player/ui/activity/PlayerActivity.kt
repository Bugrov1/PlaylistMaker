package com.example.playlistmaker.player.ui.activity

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.player.ui.BottomAdapter
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
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
    private lateinit var playlistBottomSheet:LinearLayout
    val viewModel: PlayerViewModel by viewModel { parametersOf(track) }
    private lateinit var playlistButton:ImageView
    private lateinit var  bottomSheetBehavior:BottomSheetBehavior<LinearLayout>
    private lateinit var overlay:View
    private lateinit var bottomSheetRecycler: RecyclerView

    private val adapter = BottomAdapter()

    override fun onResume() {
        super.onResume()
        viewModel.refreshBottomSheet()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        track = Gson().fromJson((intent.getStringExtra("track")), Track::class.java)
        initViews()
         bottomSheetBehavior = BottomSheetBehavior.from(playlistBottomSheet).apply { state =
            BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        setListeners()

        viewModel.observePlaylists().observe(this) {
            if (it != null) {
                renderBottomSheet(it)
            }
        }

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

    }

    fun setListeners() {
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            viewModel.playerStop()
        }

        play.setOnClickListener {
            viewModel.playClickListen()
        }
        likebutton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        playlistButton.setOnClickListener {
            bottomSheetBehavior.state= BottomSheetBehavior.STATE_COLLAPSED
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
        playlistBottomSheet = findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
        playlistButton = findViewById(R.id.playlistButton)
        overlay = findViewById(R.id.overlay)
        setupDetails(track)
        bottomSheetRecycler = findViewById(R.id.playlistsRecycler)
        bottomSheetRecycler.adapter = adapter

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun renderBottomSheet(playlists:List<Playlist>){
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()

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

    private fun renderFavoritebutton(inFavorites: Boolean) {
        if (inFavorites) {
            likebutton.setBackgroundResource(R.drawable.likebuttontrue)
        }
        if (!inFavorites) {
            likebutton.setBackgroundResource(R.drawable.likebutton)
        }

    }

}
