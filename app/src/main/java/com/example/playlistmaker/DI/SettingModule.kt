package com.example.playlistmaker.DI

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {

    single<SharedPreferences>(named("settings_pref")) {
        val application = get<Application>()
        application.getSharedPreferences(
            SettingsRepositoryImpl.PLAYLISTMAKER_SWITCH_CHECK,
            Context.MODE_PRIVATE
        )
    }


    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get(named("settings_pref")))
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




