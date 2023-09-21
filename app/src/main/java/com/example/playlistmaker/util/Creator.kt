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
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = getExternalNavigator(context)
        )
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context = context)
    }

    fun  provideSettingsInteractor(application: Application):SettingsInteractor{

        return SettingsInteractorImpl(getSettingsReository(application))
    }

    private fun getSettingsReository(application: Application): SettingsRepository {
        return SettingsRepositoryImpl(application)
    }


}