package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlayerInteractorImpl(private val player: Player) : PlayerInteractor {

    override fun setDataSource(url:String){
        player.setDataSource(url)
    }
    override fun startPlayer() {
        player.startPlayer()
        player.state=State.PLAYING
    }
    override fun pausePlayer() {
        player.pausePlayer()
        player.state=State.PAUSED
    }
    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        player.setOnPreparedListener(listener)
    }
    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        player.setOnCompletionListener(listener)
        player.state=State.NOT_PREPARED
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

    override suspend fun getState(): Flow<State> =flow{
       emit(player.state)
    }
}