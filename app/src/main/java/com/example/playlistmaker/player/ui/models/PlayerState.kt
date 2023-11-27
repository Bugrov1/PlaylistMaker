package com.example.playlistmaker.player.ui.models

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonText: String,
    val progress: String,
    var inFavorites: Boolean
) {

    class Default(inFavorites: Boolean) : PlayerState(false, "PLAY", "00:00", inFavorites)

    class Prepared(inFavorites: Boolean) : PlayerState(true, "PLAY", "00:00", inFavorites)

    class Playing(progress: String, inFavorites: Boolean) :
        PlayerState(true, "PAUSE", progress, inFavorites)

    class Paused(progress: String, inFavorites: Boolean) :
        PlayerState(true, "PLAY", progress, inFavorites)

    class Neutral(buttonText: String, progress: String, inFavorites: Boolean) :
        PlayerState(true, buttonText, progress, inFavorites)
}
