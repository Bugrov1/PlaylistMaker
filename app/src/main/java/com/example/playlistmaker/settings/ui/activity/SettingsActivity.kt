package com.example.playlistmaker.settings.ui.activity

import android.annotation.SuppressLint

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var shareButton: FrameLayout
    private lateinit var supportButton: FrameLayout
    private lateinit var licenseButton: FrameLayout
    private lateinit var themeSwitcher: SwitchMaterial


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        InitViews()
        val viewModel:SettingsViewModel by viewModel()

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
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSwitcherClicked(isChecked)
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