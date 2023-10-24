package com.example.playlistmaker.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.main.domain.model.ScreenState
import com.example.playlistmaker.main.viewmodel.MainViewModel
import com.example.playlistmaker.mediateka.ui.fragments.MediaFragment

import com.example.playlistmaker.search.ui.fragments.SearchFragment
import com.example.playlistmaker.settings.ui.fragments.SettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchButton = binding.search
        val mediaButton = binding.media
        val settingsButton = binding.settings

        val viewModel: MainViewModel by viewModel()

        viewModel.screenState.observe(viewLifecycleOwner) {
//            loadScreen(it)
        }

        searchButton.setOnClickListener {
            viewModel.onSearchButtonClicked()
        }
        mediaButton.setOnClickListener {
            viewModel.onMediaButtonClicked()
        }
        settingsButton.setOnClickListener {
            viewModel.onSettingsButtonClicked()
        }
    }



}