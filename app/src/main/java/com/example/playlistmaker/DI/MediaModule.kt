package com.example.playlistmaker.DI

import androidx.room.Room
import com.example.playlistmaker.mediateka.data.FavoritesRepositoryImpl
import com.example.playlistmaker.mediateka.data.PlaylistRepositoryImpl
import com.example.playlistmaker.mediateka.data.converters.PlaylistDBConvertor
import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.data.db.TracksInPlaylistsDatabase
import com.example.playlistmaker.mediateka.domain.FavoritesInteractorImpl
import com.example.playlistmaker.mediateka.domain.PlaylistInteractorImpl
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.domain.db.FavoritesRepository
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.ui.viewmodel.CreatePlaylistViemodel
import com.example.playlistmaker.mediateka.ui.viewmodel.EditPlaylistViewModel
import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistScreenViewmodel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {


    viewModel<FavouritesViewModel> {
        FavouritesViewModel(favoritesInteractor = get())

    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel(playlistInteractor = get())
    }

    viewModel<CreatePlaylistViemodel> {
        CreatePlaylistViemodel(playlistInteractor = get())
    }


    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(appDatabase = get(), trackDbConvertor =  get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(favoritesRepository = get())
    }

    single<PlaylistDatabase> {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "database2.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    factory { PlaylistDBConvertor() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(playlistDatabase = get(),
            playlistDbConvertor =  get(),
            tracksInPlaylistsDatabase=get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }

    single<TracksInPlaylistsDatabase> {
        Room.databaseBuilder(androidContext(), TracksInPlaylistsDatabase::class.java, "database3.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    viewModel<PlaylistScreenViewmodel> { (id: String) ->
        PlaylistScreenViewmodel(id, playlistInteractor = get(), sharingInteractor = get())
    }


    viewModel<EditPlaylistViewModel> {(id: String) ->
        EditPlaylistViewModel( id, playlistInteractor = get())
    }



}
