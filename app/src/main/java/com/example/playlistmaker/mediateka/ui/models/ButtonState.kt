package com.example.playlistmaker.mediateka.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface ButtonState {

    object Enabled :  ButtonState
    object Disabled :  ButtonState

}