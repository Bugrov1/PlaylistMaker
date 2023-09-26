package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.search.domain.models.Track


sealed class PlayerActivityState {
    object StatePlayerPause : PlayerActivityState()
    data class StatePlayerReady(
        val track: Track
    ) : PlayerActivityState()

    data class StatePlayerPlay(
        val timer: String
    ) : PlayerActivityState()
}