package com.example.playlistmaker.DI

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesAPI
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackSearchDebounceImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.api.TrackSearchDebounce
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {


    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(application = get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(historyRepository = get())
    }

    single<TrackSearchDebounce> {
        TrackSearchDebounceImpl()
    }

    single<ItunesAPI> {

        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)
    }
    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(context = get(), itunesService = get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(
            searchHistoryProvider = get(),
            tracksInteractor = get(),
            trackSearchDebounce = get()
        )
    }


}