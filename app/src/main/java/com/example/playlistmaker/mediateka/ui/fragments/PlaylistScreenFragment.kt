package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

import com.example.playlistmaker.databinding.FragmentPlaylistscreenBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistScreenViewmodel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.Adapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistScreenFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistscreenBinding

    private val adapter = Adapter()

    companion object {
        private const val ARGS_ID = "id"

        fun createArgs(id: String): Bundle =
            bundleOf(ARGS_ID to id)

    }
    val viewModel: PlaylistScreenViewmodel by viewModel { parametersOf(requireArguments().getString(ARGS_ID)) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        initViews()
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        adapter.onItemClick = {
//            val intent = Intent(requireContext(), PlayerActivity::class.java)
//            intent.putExtra("track", Gson().toJson(it))
//            startActivity(intent)
        }


    }

    private fun render(playlist: Playlist){

        binding.apply{
            playlistName.text=playlist.playlistName
            description.text=playlist.description
            tracksNumber.text=playlist.length.toString()

            Glide.with(requireActivity())
                .load(playlist.filepath)
                .placeholder(R.drawable.placeholderbig)
                .centerCrop()
                .into(albumCover)
        }
    }
}