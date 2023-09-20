package com.example.playlistmaker.player.ui.models

sealed class PlayerActivityState {

    object StatePlayerPlay : PlayerActivityState()
    object StatePlayerPause : PlayerActivityState()
    object StatePlayerReady : PlayerActivityState()

}