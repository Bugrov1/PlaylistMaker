package com.example.playlistmaker.domain

import com.example.playlistmaker.ui.player.PlayerState


interface Player {
    var playerState: PlayerState
    fun preparePlayer(url:String,)
    fun startPlayer()
    fun pausePlayer()

    fun setOnPreparedListener(listener: (() -> Unit)?)

    fun setOnCompletionListener(listener: (() -> Unit)?)

    fun release()

   fun currentPosition():Int

}
