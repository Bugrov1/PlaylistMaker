package com.example.playlistmaker.util

import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.PlayerImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TRACK_SEARCH_HISTORY
import com.example.playlistmaker.domain.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.repository.SearchHistoryInteractor
import com.example.playlistmaker.presentation.player.PlayerController
import com.example.playlistmaker.presentation.player.PlayerView
import com.example.playlistmaker.presentation.trackSearch.SearchPresenter
import com.example.playlistmaker.presentation.trackSearch.SearchView
import com.example.playlistmaker.ui.tracks.Adapter

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

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun provideSearchHistoryRepositoryImpl(): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(application)
    }

    fun provideSearchHistory(): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepositoryImpl())
    }

    fun provideSearchPresenter(searchView: SearchView,context: Context): SearchPresenter {
        return SearchPresenter(view = searchView,context = context)
    }
    fun providePlayerController(playerView: PlayerView,
                                albumCoverUrl:String?,
                                url:String,
                                trackNameText:String?,
                                artistNameText:String?,
                                trackTime:String,
                                albumNameValueText:String,
                                yearValueText:CharSequence,
                                genreValueText:String,
                                countryValueText:String


    ): PlayerController {
        return PlayerController(playerView,albumCoverUrl,url,trackNameText,artistNameText,
            trackTime,albumNameValueText,yearValueText,genreValueText,countryValueText)
    }


}