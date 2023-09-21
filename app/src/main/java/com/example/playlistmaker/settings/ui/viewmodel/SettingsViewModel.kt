package com.example.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.util.Creator

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val application: App
) : AndroidViewModel(application) {

    private var darkTheme = false
    private val _themeSwitcherState = MutableLiveData<Boolean>()
    val themeSwitcherState: LiveData<Boolean> = _themeSwitcherState

    init {
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        _themeSwitcherState.value = darkTheme
    }

    fun onSwitcherClicked(isChecked: Boolean) {
        _themeSwitcherState.value = isChecked
        settingsInteractor.updateThemeSetting(ThemeSettings(darkTheme = isChecked))
        switchTheme(isChecked)
    }
    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }
    fun onLicenseClicked() {
        sharingInteractor.openTerms()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                SettingsViewModel(
                    settingsInteractor = Creator.provideSettingsInteractor(application),
                    sharingInteractor = Creator.provideSharingInteractor(application),
                    application = application,
                )
            }
        }
    }
}