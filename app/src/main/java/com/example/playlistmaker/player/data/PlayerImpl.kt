package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.Player
import com.example.playlistmaker.player.domain.models.PlayerState


class PlayerImpl() : Player {
    private var mediaPlayer = MediaPlayer()

    override var playerState = PlayerState.NOT_PREPARED

    override fun preparePlayer(url:String
    ) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        playerState = PlayerState.READY

    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED

    }

    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnPreparedListener{ listener?.invoke()}

    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnCompletionListener { listener?.invoke() }
        playerState = PlayerState.READY
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }


}