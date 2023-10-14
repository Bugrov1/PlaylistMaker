package com.example.playlistmaker.settings.data.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings


class SettingsRepositoryImpl(val sharedPreferences: SharedPreferences) : SettingsRepository {


    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            darkTheme = sharedPreferences.getBoolean(CHECKED_KEY, false)
        )
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences
            .edit()
            .putBoolean(CHECKED_KEY, settings.darkTheme)
            .apply()
    }

    companion object {
        const val PLAYLISTMAKER_SWITCH_CHECK = "playlistmaker_switch_check"
        const val CHECKED_KEY = "isChecked"
    }

}