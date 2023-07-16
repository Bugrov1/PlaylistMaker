package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

const val TRACK_SEARCH_HISTORY = "track_search_history"
const val TRACKS_LIST_KEY = "track_list_key"

class SearchHistory(var sharedPref: SharedPreferences) {

    fun read(): Array<Track>? {
        var json = sharedPref.getString(TRACKS_LIST_KEY, null)
        return Gson().fromJson(json, Array<Track>::class.java)
    }


    fun write(track: Track) {
        var historyList = read()
        var historyListMutable = historyList?.toMutableList()
        //historyListMutable?.clear()

        var trackToRemove = false
        if (historyListMutable != null) {
            for (i in historyListMutable) {
                if (i.trackId == track.trackId) {
                    Log.v(TAG, "POVTORENIE " + i.trackName)
                    var indexElement = historyListMutable.indexOf(i)

                    Log.v(TAG, i.trackName + " DELETED from " + indexElement)
                    trackToRemove = true

                }
            }
        }
        if (trackToRemove) {
            historyListMutable?.remove(track)
        }

        if (historyListMutable != null) {
            if (historyListMutable.size == 10) {
                Log.v(TAG, "HISTORY achieved 10 tracks");
                Log.v(TAG, historyListMutable[9].trackName + " REMOVED from 9");
                historyListMutable?.removeAt(9)

                historyListMutable?.add(0, track)
                Log.v(TAG, track.trackName + " ADDED to 0")
            } else {
                historyListMutable?.add(0, track)
                Log.v(TAG, track.trackName + " ADDED to 0")
            }
        }

        if (historyListMutable != null) {
            println("Current List")
            for (i in historyListMutable) {
                println(i.trackName)
            }
        }

        if (historyListMutable != null) {
            historyList = historyListMutable.toTypedArray()
        }
        val json = Gson().toJson(historyList) //перевод в текст и сохранение в файл
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()

    }

    fun clear() {
        var historyList = read()
        var historyListMutable = historyList?.toMutableList()
        historyListMutable?.clear()

        val json = Gson().toJson(historyListMutable) //перевод в текст и сохранение в файл
        sharedPref.edit()
            .putString(TRACKS_LIST_KEY, json)
            .apply()
    }
}