package com.example.playlistmaker.mediateka.domain.model

import android.net.Uri

data class Playlist(
    val id:Long?,
    val playlistName: String,
    val description: String?,
    val filepath: Uri?,
    val tracks: String?,
    val length: Int?,
    )
