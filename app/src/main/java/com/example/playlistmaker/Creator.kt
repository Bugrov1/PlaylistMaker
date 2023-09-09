package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.PlayerImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TRACK_SEARCH_HISTORY
import com.example.playlistmaker.domain.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.repository.SearchHistoryInteractor

object Creator {

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractor(PlayerImpl())
    }
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun provideSearchHistoryRepositoryImpl(context:Context): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(context)
    }

    fun provideSearchHistory(context:Context): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepositoryImpl(context))
    }


}