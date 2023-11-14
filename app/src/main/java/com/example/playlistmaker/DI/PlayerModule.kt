package com.example.playlistmaker.DI

import com.example.playlistmaker.player.data.PlayerImpl
import com.example.playlistmaker.player.domain.Player
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {


    factory<Player> {
        PlayerImpl()
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(player = get())
    }

    viewModel<PlayerViewModel> { (track: Track) ->
        PlayerViewModel(track, mediaPlayer = get())
    }

//    viewModel<PlayerViewModel> { (track: Track) ->
//        PlayerViewModel(track, mediaPlayer = get())
//    }


}