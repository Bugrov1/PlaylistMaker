package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.api.PlayerInteractor



class PlayerInteractorImpl(private val player: Player) : PlayerInteractor {

    override fun setDataSource(url:String){
        player.setDataSource(url)
    }
    override fun startPlayer() {
        player.startPlayer()
    }
    override fun pausePlayer() {
        player.pausePlayer()
    }
    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        player.setOnPreparedListener(listener)
    }
    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        player.setOnCompletionListener(listener)
    }
    override fun release() {
        player.release()
    }
    override fun currentPosition(): String {
        return player.currentPosition()
    }

    override fun stop() {
        player.stop()
    }

    override fun isPlaying(): Boolean {
       return player.isPlaying()
    }
}