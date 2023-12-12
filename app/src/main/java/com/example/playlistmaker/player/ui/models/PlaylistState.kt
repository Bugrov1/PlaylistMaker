package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.mediateka.ui.models.ButtonState

sealed class PlaylistState( val text: String){

    class inPlaylist (text: String): PlaylistState(text)
    class notInPlaylist(text: String) : PlaylistState(text)
}
