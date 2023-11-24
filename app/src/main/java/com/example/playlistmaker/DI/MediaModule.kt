package com.example.playlistmaker.DI

import androidx.room.Room
import com.example.playlistmaker.mediateka.data.FavoritesRepositoryImpl
import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.mediateka.domain.FavoritesInteractorImpl
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.domain.db.FavoritesRepository
import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
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
