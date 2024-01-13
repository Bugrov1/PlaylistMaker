package com.example.playlistmaker.settings.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var shareButton: FrameLayout
    private lateinit var supportButton: FrameLayout
    private lateinit var licenseButton: FrameLayout
    private lateinit var themeSwitcher: SwitchCompat
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InitViews()
        val viewModel:SettingsViewModel by viewModel()

        viewModel.themeSwitcherState.observe(viewLifecycleOwner) { isChecked ->
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

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSwitcherClicked(isChecked)
        }
    }

    fun InitViews() {
        shareButton = binding.shareButton
        supportButton = binding.supportButton
        licenseButton = binding.usersLicense
        themeSwitcher = binding.themeSwitcher
    }

}