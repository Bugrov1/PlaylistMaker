package com.example.playlistmaker.mediateka.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

import com.example.playlistmaker.databinding.FragmentPlaylistscreenBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.DialogStatus
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistScreenViewmodel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.player.ui.TracksEndingCount
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistScreenFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistscreenBinding

    private val adapter = Adapter()
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state =
                BottomSheetBehavior.STATE_COLLAPSED

        }


        binding.playlistsRecycler.adapter=adapter

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.duration.observe(viewLifecycleOwner) {
           binding.timeTotal.text=it
        }

        viewModel.tracks.observe(viewLifecycleOwner) {
           renderBottomSheet(it)
        }

        adapter.onItemClick = {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("track", Gson().toJson(it))
            startActivity(intent)
        }

        adapter.onLongItemclick = {
            deleteTrack(it)
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }


    }
    private fun deleteTrack(track:Track){
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNeutralButton("Отмена") { dialog, which ->
            }.setPositiveButton("Удалить") { dialog, which ->
                viewModel.remove(track)
            }
        confirmDialog.show()
    }

    private fun render(playlist: Playlist){

        binding.apply{
            playlistName.text=playlist.playlistName
            description.text=playlist.description
            tracksNumber.text= playlist.length?.let { TracksEndingCount().tracksString(it)  }

            Glide.with(requireActivity())
                .load(playlist.filepath)
                .placeholder(R.drawable.placeholderbig)
                .centerCrop()
                .into(albumCover)
        }
    }

    private fun renderBottomSheet(tracks: List<Track>) {
        if (tracks.size==0){
            binding.playlistsBottomSheet.visibility=View.GONE

        }
        else{adapter.tracks.clear()
            adapter.tracks.addAll(tracks)
            adapter.notifyDataSetChanged()}

    }

}

