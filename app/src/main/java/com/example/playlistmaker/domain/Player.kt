package com.example.playlistmaker.domain



import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView

interface Player {

    fun preparePlayer(play:ImageButton, url:String,  timer:TextView,  mainThreadHandler: Handler?)
    fun startPlayer(play:ImageButton)
    fun pausePlayer(play:ImageButton)
    fun playbackControl(play:ImageButton)
}