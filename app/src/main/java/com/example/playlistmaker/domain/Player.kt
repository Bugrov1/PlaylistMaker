package com.example.playlistmaker.domain



interface Player {

    fun preparePlayer(url:String,)
    fun startPlayer()
    fun pausePlayer()

    fun setOnPreparedListener(listener: (() -> Unit)?)

    fun setOnCompletionListener(listener: (() -> Unit)?)

    fun release()

   fun currentPosition():Int

}
