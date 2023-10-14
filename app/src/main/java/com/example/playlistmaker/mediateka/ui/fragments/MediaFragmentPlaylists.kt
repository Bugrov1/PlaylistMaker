package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMediaPlaylistsBinding
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentPlaylists : Fragment() {



    companion object {
        private const val TEXT = "text"

        fun newInstance(text: String) = MediaFragmentPlaylists().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }
    }
    val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentMediaPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
        setListeners()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderText.text = requireArguments().getString(TEXT).toString()
    }

    fun setListeners() {
        binding.createNewPlaylist.setOnClickListener {
            viewModel.createNewList()
        }

    }


}