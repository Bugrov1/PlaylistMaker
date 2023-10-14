package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMediaFavoritesBinding
import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavourites: Fragment() {

    companion object {
        private const val TEXT = "text"

        fun newInstance(text: String) = MediaFragmentFavourites().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }
    }
    val viewModel: FavouritesViewModel by viewModel()

    private lateinit var binding: FragmentMediaFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMediaFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderText.text = requireArguments().getString(TEXT).toString()
    }

}