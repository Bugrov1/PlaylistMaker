package com.example.playlistmaker.util

import android.app.Activity
import android.content.Context
import com.example.playlistmaker.data.PlayerImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.repository.SearchHistoryInteractor
import com.example.playlistmaker.presentation.PlayerController
import com.example.playlistmaker.presentation.TrackSearchController
import com.example.playlistmaker.ui.Adapter

object Creator {

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractor(PlayerImpl())
    }
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun provideSearchHistoryRepositoryImpl(context:Context): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(context)
    }

    fun provideSearchHistory(context:Context): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepositoryImpl(context))
    }

    fun provideTrackSearchController(activity: Activity, adapter: Adapter,historyAdapter: Adapter): TrackSearchController {
        return TrackSearchController(activity, adapter,historyAdapter)
    }
    fun providePlayerController(activity: Activity): PlayerController {
        return PlayerController(activity)
    }


}