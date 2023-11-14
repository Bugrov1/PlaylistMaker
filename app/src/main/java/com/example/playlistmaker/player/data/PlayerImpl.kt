package com.example.playlistmaker.player.data

import android.media.MediaPlayer

import com.example.playlistmaker.player.domain.Player
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerImpl() : Player {

    private var mediaPlayer = MediaPlayer()

    override fun setDataSource(url: String) {
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
        mediaPlayer.setOnPreparedListener { listener?.invoke() }

    }

    override fun setOnCompletionListener(listener: (() -> Unit)?) {
        mediaPlayer.setOnCompletionListener { listener?.invoke() }
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): String {
        val timerText = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
        return timerText
    }

    override fun stop() {
        mediaPlayer.stop()
    }
    override fun isPlaying():Boolean{
       return  mediaPlayer.isPlaying
    }


}