package com.example.playlistmaker.DI

import androidx.room.Room
import com.example.playlistmaker.mediateka.data.FavoritesRepositoryImpl
import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.mediateka.domain.FavoritesInteractorImpl
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.domain.db.FavoritesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }


    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(appDatabase = get(), trackDbConvertor =  get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(favoritesRepository = get())
    }

}