package com.example.playlistmaker.search.data.repository

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson


const val TRACK_SEARCH_HISTORY = "track_search_history"
const val TRACKS_LIST_KEY = "track_list_key"

class SearchHistoryRepositoryImp(application: Application) : SearchHistoryRepository {

    private val listMaxSize = 10
    private val sharedPref = application.getSharedPreferences(
        TRACK_SEARCH_HISTORY,
        Context.MODE_PRIVATE
    )
    override fun read(): Array<Track>? {
        val json = sharedPref.getString(TRACKS_LIST_KEY, null)
        println("$json json")
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    override fun write(track: Track) {
        var historyList = read() ?: emptyArray()
        println(historyList)
        val historyListMutable = historyList.toMutableList()
        Log.v(ContentValues.TAG, "historyListMutable is $historyListMutable")


        var trackToRemove = false

        for (i in historyListMutable) {
            if (i.trackId == track.trackId) {
                Log.v(ContentValues.TAG, "repeated " + i.trackName)
                val indexElement = historyListMutable.indexOf(i)

                Log.v(ContentValues.TAG, i.trackName + " DELETED from " + indexElement)
                trackToRemove = true

            }
        }

        if (trackToRemove) {
            historyListMutable.remove(track)
        }

        if (historyListMutable.size == listMaxSize) {
            Log.v(ContentValues.TAG, "HISTORY achieved 10 tracks")
            Log.v(ContentValues.TAG, historyListMutable[9].trackName + " REMOVED from 9")
            historyListMutable.removeAt(9)

            historyListMutable.add(0, track)
            Log.v(ContentValues.TAG, track.trackName + " ADDED to 0")
        } else {
            historyListMutable.add(0, track)
            Log.v(ContentValues.TAG, track.trackName + " ADDED to 0")
        }


        println("Current List")
        for (i in historyListMutable) {
            println(i.trackName)
        }

        historyList = historyListMutable.toTypedArray()
        val json = Gson().toJson(historyList)
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()


    }

    override fun clear() {
        val historyList = read() ?: emptyArray()
        val historyListMutable = historyList.toMutableList()
        historyListMutable.clear()

        val json = Gson().toJson(historyListMutable)
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()
    }

}



