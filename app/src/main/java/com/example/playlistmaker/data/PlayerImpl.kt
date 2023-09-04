package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.Player


class PlayerImpl() : Player {
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(url:String
    ) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()

    }

    override fun setOnPreparedListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnPreparedListener{ listener?.invoke()}
    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnPreparedListener{ listener?.invoke()}
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }


}