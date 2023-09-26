package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState

sealed class PlayerActivityState {

//    object StatePlayerPlay : PlayerActivityState()
    object StatePlayerPause : PlayerActivityState()
    object StatePlayerReady : PlayerActivityState()

    data class StatePlayerPlay (
        val timer:String
    ) : PlayerActivityState()

}