package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.models.PlayerState


class PlayerInteractor(private val player: Player) {

    fun preparePlayer(url:String){
        player.preparePlayer(url=url)
    }
    fun startPlayer(){
        player.startPlayer()
    }
    fun pausePlayer(){
        player.pausePlayer()
    }


    fun setOnPreparedListener(listener: (() -> Unit)?) {
        player.setOnPreparedListener(listener)
    }


    fun setOnCompletionListener(listener: (() -> Unit)?) {
        player.setOnCompletionListener(listener)
    }

    fun release(){
        player.release()
    }

    fun currentPosition(): Int {
        return player.currentPosition()
    }

    fun getState(): PlayerState {
        return player.playerState
    }

}