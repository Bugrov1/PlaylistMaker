package com.example.playlistmaker.main.domain.model

sealed interface ScreenState{

    object Search : ScreenState
    object Mediateka : ScreenState
    object Settings : ScreenState
}