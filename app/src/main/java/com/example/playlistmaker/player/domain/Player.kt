package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.models.PlayerState


interface Player {
    var playerState: PlayerState

    fun startPlayer()
    fun pausePlayer()
    fun setOnPreparedListener(listener: (() -> Unit)?)
    fun setOnCompletionListener(listener: (() -> Unit)?)
    fun release()
    fun currentPosition(): String
    fun stop()
}
