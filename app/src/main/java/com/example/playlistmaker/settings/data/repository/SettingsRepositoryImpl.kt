package com.example.playlistmaker.settings.data.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings


class SettingsRepositoryImpl(application: Application) : SettingsRepository {

    val sharedPreferences = application.getSharedPreferences(
        PLAYLISTMAKER_SWITCH_CHECK,
        MODE_PRIVATE
    )
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
companion object{
    const val PLAYLISTMAKER_SWITCH_CHECK = "playlistmaker_switch_check"
    const val CHECKED_KEY = "isChecked"}

}