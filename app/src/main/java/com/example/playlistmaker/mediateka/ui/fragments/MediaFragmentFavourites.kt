package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaFavoritesBinding
import com.example.playlistmaker.mediateka.ui.models.FavoritesState
import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavourites : Fragment() {


    val viewModel: FavouritesViewModel by viewModel()

    private lateinit var binding: FragmentMediaFavoritesBinding

    private val adapter = Adapter()


    override fun onResume() {
        super.onResume()
        viewModel.refresh()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
        binding.placeholderText.text = requireArguments().getString(TEXT).toString()
        initViews()
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        adapter.onItemClick = {
            startPlayer(it)
        }


    }

    private fun startPlayer(track: Track) {
        val trackGson = Gson().toJson(track)
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(trackGson)
        )
    }

    private fun initViews() {
        binding.historyList.adapter = adapter

    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmpty()
            is FavoritesState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.placeholderText.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.historyList.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.historyList.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val TEXT = "text"

        fun newInstance(text: String) = MediaFragmentFavourites().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }
    }


}