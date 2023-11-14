package com.example.playlistmaker.player.domain


interface Player {


    fun setDataSource(url:String)
    fun startPlayer()
    fun pausePlayer()
    fun setOnPreparedListener(listener: (() -> Unit)?)
    fun setOnCompletionListener(listener: (() -> Unit)?)
    fun release()
    fun currentPosition(): String
    fun stop()

    fun isPlaying():Boolean
}