package com.example.playlistmaker.presentation.player

interface PlayerView {

    fun setupAlbumCover(url: String?)

    fun setPlayButtonStatus(status: Boolean)

    fun setupTextValues(
        trackNameText: String?,
        artistNameText: String?,
        trackTime: String,
        albumNameValueText: String,
        yearValueText: CharSequence,
        genreValueText: String,
        countryValueText: String
    )

    fun setTimerText(text: String)
}