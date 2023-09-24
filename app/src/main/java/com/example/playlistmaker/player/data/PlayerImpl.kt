package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.Player
import com.example.playlistmaker.player.domain.models.PlayerState


class PlayerImpl(private val url: String) : Player {
    private var mediaPlayer = MediaPlayer()

    override var playerState = PlayerState.NOT_PREPARED


    init {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        setOnPreparedListener {
            playerState = PlayerState.READY
        }
        setOnPreparedListener {
            playerState = PlayerState.READY
        }
    }



    override fun startPlayer() {
        if (playerState == PlayerState.NOT_PREPARED){
            return
        }
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
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun stop() {
        mediaPlayer.stop()
    }


}