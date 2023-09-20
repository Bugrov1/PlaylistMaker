package com.example.playlistmaker.util

import android.app.Application
import android.content.Context
import com.example.playlistmaker.player.data.PlayerImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImp
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryInteractor

object Creator {

    lateinit var application: Application

     fun registryApplication(application: Application){
         this.application = application
     }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractor(PlayerImpl())
    }
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TracksInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun provideSearchHistoryRepositoryImpl(): SearchHistoryRepositoryImp {
        return SearchHistoryRepositoryImp(application)
    }

    fun provideSearchHistory(): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepositoryImpl())
    }


}