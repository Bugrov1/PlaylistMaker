package com.example.playlistmaker.DI

import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single<SettingsRepository> {
        SettingsRepositoryImpl(application = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }


    viewModel<SettingsViewModel> {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }


}




