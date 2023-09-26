package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.PlayerState

interface PlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun setOnPreparedListener(listener: (() -> Unit)?)
    fun setOnCompletionListener(listener: (() -> Unit)?)
    fun release()
    fun currentPosition(): String
    fun getState(): PlayerState
    fun stop()
}