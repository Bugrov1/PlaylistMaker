package com.example.playlistmaker.main.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.main.domain.model.ScreenState
import com.example.playlistmaker.main.viewmodel.MainViewModel
import com.example.playlistmaker.settings.ui.activity.SettingsActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val mediaButton = findViewById<Button>(R.id.media)
        val settingsButton = findViewById<Button>(R.id.settings)

        val viewModel = ViewModelProvider(
            this,
            MainViewModel.getViewModelFactory()
        )[MainViewModel::class.java]

        viewModel.screenState.observe(this) {
            loadScreen(it)
        }

        searchButton.setOnClickListener {
            viewModel.onSeachButtonClicked()
        }
        mediaButton.setOnClickListener {
            viewModel.onMediaButtonClicked()
        }
        settingsButton.setOnClickListener {
            viewModel.onSettingsButtonClicked()
        }
    }

    fun loadScreen(state: ScreenState) {
        when (state) {
            ScreenState.Search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }

            ScreenState.Mediateka -> {
                startActivity(Intent(this, MediaActivity::class.java))
            }

            ScreenState.Settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }
}


