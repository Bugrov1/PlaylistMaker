package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.ui.activity.CHECKED_KEY
import com.example.playlistmaker.settings.ui.activity.PLAYLISTMAKER_SWITCH_CHECK
import com.example.playlistmaker.util.Creator


class App : Application() {


    var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SWITCH_CHECK, MODE_PRIVATE)
        darkTheme = sharedPrefs.getString(CHECKED_KEY, false.toString()).toBoolean()
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        Creator.registryApplication(this)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SWITCH_CHECK, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                sharedPrefs.edit().putString(CHECKED_KEY, "true").apply()
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                sharedPrefs.edit().putString(CHECKED_KEY, "false").apply()
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
