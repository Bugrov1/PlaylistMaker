package com.example.playlistmaker.search.data.repository

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.playlistmaker.search.domain.api.TrackSearchDebounce

class TrackSearchDebounceImpl : TrackSearchDebounce {
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun searchDebounce(searchRequest: () -> Unit) {

        val searchRunnable = Runnable { searchRequest() }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

}