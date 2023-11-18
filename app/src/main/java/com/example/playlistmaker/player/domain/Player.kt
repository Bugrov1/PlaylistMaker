package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.models.State


interface Player {
    var state: State

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