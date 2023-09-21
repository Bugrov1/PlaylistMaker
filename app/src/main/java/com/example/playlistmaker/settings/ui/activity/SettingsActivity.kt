package com.example.playlistmaker.settings.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var shareButton: FrameLayout
    private lateinit var supportButton: FrameLayout
    private lateinit var licenseButton: FrameLayout
    private lateinit var themeSwitcher: SwitchMaterial

    private lateinit var viewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        InitViews()
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        viewModel.themeSwitcherState.observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        shareButton.setOnClickListener {
            viewModel.onShareAppClicked()

        }

        supportButton.setOnClickListener {
            viewModel.onSupportClicked()
        }

        licenseButton.setOnClickListener {
            viewModel.onLicenseClicked()

        }

        backButton.setOnClickListener {
            finish()
        }
        themeSwitcher.setOnCheckedChangeListener{ _, checked ->
            viewModel.onSwitcherClicked(checked)
        }


    }

    fun InitViews() {
        backButton = findViewById<ImageButton>(R.id.back)
        shareButton = findViewById<FrameLayout>(R.id.share_button)
        supportButton = findViewById<FrameLayout>(R.id.support_button)
        licenseButton = findViewById<FrameLayout>(R.id.users_license)
        themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
    }

}