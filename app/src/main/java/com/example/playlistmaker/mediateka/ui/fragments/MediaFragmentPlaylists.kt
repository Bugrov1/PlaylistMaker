package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaPlaylistsBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.PlaylistAdapter
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentPlaylists : Fragment() {

    private val adapter = PlaylistAdapter()
    private lateinit var placeholderImage: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderText: TextView
    override fun onResume() {
        super.onResume()
        viewModel.refresh()

    }

    val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentMediaPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager= GridLayoutManager(requireContext(), /*Количество столбцов*/ 2)
        recyclerView.adapter = adapter
        placeholderImage = binding.placeholderImage
        placeholderText = binding.placeholderText
        viewModel.refresh()
        binding.placeholderText.text = requireArguments().getString(TEXT).toString()
        viewModel.state.observe(viewLifecycleOwner) {
            adapter.playlists.clear()
            render(it)

        }
        setListeners()
    }

    fun setListeners() {
        binding.createNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        adapter.onItemClick = {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistScreenFragment,PlaylistScreenFragment.createArgs(it.id.toString()))

        }

    }
    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        placeholderText.visibility= View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showContent(playlists: List<Playlist>) {
        recyclerView.visibility = View.VISIBLE
        placeholderImage.visibility = View.GONE
        placeholderText.visibility=View.GONE
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }



    companion object {
        private const val TEXT = "text"

        fun newInstance(text: String) = MediaFragmentPlaylists().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }
    }


}