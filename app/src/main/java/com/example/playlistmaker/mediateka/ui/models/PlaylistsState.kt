package com.example.playlistmaker.mediateka.ui.models

import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistsState{

    object Empty :  PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ) :  PlaylistsState
}