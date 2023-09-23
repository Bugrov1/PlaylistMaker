package com.example.playlistmaker

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl.Companion.CHECKED_KEY
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl.Companion.PLAYLISTMAKER_SWITCH_CHECK

import com.example.playlistmaker.util.Creator


class App : Application() {


    private var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        Creator.registryApplication(this)
        val settingsInteractor = Creator.provideSettingsInteractor()

        Log.v(ContentValues.TAG, "App created is $darkTheme")
        darkTheme = settingsInteractor.getThemeSettings().darkTheme



        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )


    }

//    fun switchTheme(darkThemeEnabled: Boolean) {
//        darkTheme = darkThemeEnabled
//        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SWITCH_CHECK, MODE_PRIVATE)
//        AppCompatDelegate.setDefaultNightMode(
//            if (darkThemeEnabled) {
//                sharedPrefs.edit().putString(CHECKED_KEY, "true").apply()
//                AppCompatDelegate.MODE_NIGHT_YES
//
//            } else {
//                sharedPrefs.edit().putString(CHECKED_KEY, "false").apply()
//                AppCompatDelegate.MODE_NIGHT_NO
//            }
//        )
//    }
}
