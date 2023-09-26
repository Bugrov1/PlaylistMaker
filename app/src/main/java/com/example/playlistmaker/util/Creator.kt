package com.example.playlistmaker.util

import android.app.Application
import android.content.Context
import com.example.playlistmaker.player.data.PlayerImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImp
import com.example.playlistmaker.search.data.repository.TrackSearchDebounceImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.api.TrackSearchDebounce
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    lateinit var application: Application

     fun registryApplication(application: Application){
         this.application = application

     }

    fun providePlayerInteractor(url:String): PlayerInteractor {
        return PlayerInteractorImpl(PlayerImpl(url=url ))
    }
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context= application))
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun provideSearchHistoryRepositoryImpl(): SearchHistoryRepository {
        return SearchHistoryRepositoryImp(application)
    }

    fun provideSearchHistory(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepositoryImpl())
    }
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = getExternalNavigator(application)
        )
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context = context)
    }

    fun  provideSettingsInteractor():SettingsInteractor{
        return SettingsInteractorImpl(getSettingsReository(application))
    }

    private fun getSettingsReository(application: Application): SettingsRepository {
        return SettingsRepositoryImpl(application)
    }

     fun getSearchDebounce(): TrackSearchDebounce {
        return TrackSearchDebounceImpl()
    }

//    fun getHandler(): PlayerHandler {
//        return PlayerHandlerImpl()
//    }


}