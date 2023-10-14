package com.example.playlistmaker

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.DI.mainModule
import com.example.playlistmaker.DI.mediaModule
import com.example.playlistmaker.DI.playerModule
import com.example.playlistmaker.DI.searchModule
import com.example.playlistmaker.DI.settingsModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class App : Application() {


    private var darkTheme = false


    override fun onCreate() {
        super.onCreate()

        Log.v(ContentValues.TAG, "App created is $darkTheme")
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(settingsModule, mainModule, playerModule, searchModule, mediaModule))
        }
        val settingsInteractor: SettingsInteractor by inject()
        darkTheme = settingsInteractor.getThemeSettings().darkTheme



        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )


    }

}
