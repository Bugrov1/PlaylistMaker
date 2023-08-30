package com.example.playlistmaker

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.domain.Track
import com.google.gson.Gson

const val TRACK_SEARCH_HISTORY = "track_search_history"
const val TRACKS_LIST_KEY = "track_list_key"

class SearchHistory(var sharedPref: SharedPreferences) {
    val listMaxSize = 10
    fun read(): Array<Track>? {
        val json = sharedPref.getString(TRACKS_LIST_KEY, null)
        println(json+" json")
        return Gson().fromJson(json, Array<Track>::class.java)
    }


    fun write(track: Track) {
        var historyList = read()?:emptyArray()
        println(historyList)
        val historyListMutable = historyList.toMutableList()
        Log.v(TAG, "historyListMutable is $historyListMutable")


        var trackToRemove = false

        for (i in historyListMutable) {
            if (i.trackId == track.trackId) {
                Log.v(TAG, "POVTORENIE " + i.trackName)
                val indexElement = historyListMutable.indexOf(i)

                Log.v(TAG, i.trackName + " DELETED from " + indexElement)
                trackToRemove = true

            }
        }

        if (trackToRemove) {
            historyListMutable.remove(track)
        }

        if (historyListMutable.size == listMaxSize) {
            Log.v(TAG, "HISTORY achieved 10 tracks")
            Log.v(TAG, historyListMutable[9].trackName + " REMOVED from 9")
            historyListMutable.removeAt(9)

            historyListMutable.add(0, track)
            Log.v(TAG, track.trackName + " ADDED to 0")
        } else {
            historyListMutable.add(0, track)
            Log.v(TAG, track.trackName + " ADDED to 0")
        }


        println("Current List")
        for (i in historyListMutable) {
            println(i.trackName)
        }

        historyList = historyListMutable.toTypedArray()
        val json = Gson().toJson(historyList) //перевод в текст и сохранение в файл
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()



    }

    fun clear() {
        val historyList = read()?:emptyArray()
        val historyListMutable = historyList.toMutableList()
        historyListMutable.clear()

        val json = Gson().toJson(historyListMutable) //перевод в текст и сохранение в файл
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()
    }
}