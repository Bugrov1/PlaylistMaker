package com.example.playlistmaker.mediateka.ui.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentMediaFavoritesBinding
import com.example.playlistmaker.mediateka.ui.models.FavoritesState
import com.example.playlistmaker.mediateka.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.example.playlistmaker.search.ui.models.SearchState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavourites : Fragment() {

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

    private val adapter = Adapter()


    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var historyList: RecyclerView

    override fun onResume() {
        super.onResume()
        Log.v("TESSSSSSSSSSST","RESUMED")
        viewModel.refresh()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderText.text = requireArguments().getString(TEXT).toString()
        initViews()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            Log.v("TESSSSSSSSSSST","$it")
        }

        adapter.onItemClick = {
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("track", Gson().toJson(it))
                startActivity(intent)
        }



    }

    private fun initViews() {
        placeholderImage = binding.placeholderImage
        placeholderText = binding.placeholderText
        historyList = binding.historyList
        historyList.adapter = adapter

    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmpty()
            is FavoritesState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        placeholderText.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        historyList.visibility = View.VISIBLE
        placeholderText.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }


}