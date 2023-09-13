package com.example.playlistmaker.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.api.TrackInteractor

class SearchActivity : Activity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    private val adapter = Adapter()
    private val adapterHistory = Adapter()

    private val trackSearchController = Creator.provideTrackSearchController(this, adapter,adapterHistory)

    //private val tracksInteractor = Creator.provideTrackInteractor(this)

    //private val searchHistoryProvider = Creator.provideSearchHistory(this)
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //var sharedPreferences = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
        //val history = SearchHistory(sharedPreferences).read()?.toCollection(ArrayList())
        val searchHistoryProvider = Creator.provideSearchHistory(this)
        //---val history = searchHistoryProvider.read()

        adapter.onItemClick = {
            if (clickDebounce()) {

                searchHistoryProvider.write(it)

                //adapter.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)
                putExtra(intent, it)
                startActivity(intent)
            }

        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                searchHistoryProvider.write(it)

                // для обновления списка онлайн
                adapterHistory.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)//PlayerActivity
                putExtra(intent, it)
                startActivity(intent)
                adapterHistory.tracks =
                    searchHistoryProvider.read()?.toCollection(ArrayList())!!



            }
        }
        trackSearchController.onCreate()

    }

    override fun onDestroy() {
        super.onDestroy()
        trackSearchController.onDestroy()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private fun putExtra(intent: Intent, track: Track) {
        intent.putExtra("trackName", track.trackName)
        intent.putExtra("artistName", track.artistName)
        intent.putExtra("trackTimeMillis", track.trackTimeMillis)
        intent.putExtra("artworkUrl100", track.artworkUrl100)
        intent.putExtra("collectionName", track.collectionName)
        intent.putExtra("releaseDate", track.releaseDate)
        intent.putExtra("primaryGenreName", track.primaryGenreName)
        intent.putExtra("country", track.country)
        intent.putExtra("previewUrl", track.previewUrl)
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        trackSearchController.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackSearchController.onRestoreInstanceState(savedInstanceState)

    }


}