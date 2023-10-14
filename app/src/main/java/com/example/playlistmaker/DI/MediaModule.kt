package com.example.playlistmaker.DI

import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {


    viewModel<FavouritesViewModel> {
        FavouritesViewModel()

    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel()
    }
}
