package com.example.playlistmaker.mediateka.ui.fragments

import android.content.ContentProvider
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

import com.example.playlistmaker.databinding.FragmentPlaylistscreenBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.PlaylistAdapter
import com.example.playlistmaker.mediateka.ui.models.DialogStatus
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistScreenViewmodel
import com.example.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.player.ui.BottomAdapter
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
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistScreenFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistscreenBinding

    private val adapter = Adapter()
    private val adapterShare = BottomAdapter()
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetShareBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var tracks: List<Track>
    private lateinit var playlist: Playlist
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

        bottomSheetShareBehavior = BottomSheetBehavior.from(binding.sharebottomsheet).apply {
            state =
                BottomSheetBehavior.STATE_HIDDEN

        }



        binding.playlistsRecycler.adapter=adapter
        binding.shareRecycler.adapter =adapterShare

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
            playlist = it
            adapterShare.playlists.clear()
            adapterShare.playlists.add(it)
            adapterShare.notifyDataSetChanged()
        }
        viewModel.duration.observe(viewLifecycleOwner) {
           binding.timeTotal.text=it
        }

        viewModel.tracks.observe(viewLifecycleOwner) {
           renderBottomSheet(it)
            tracks = it
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
        binding.shareButton.setOnClickListener {
            shareDialog()
        }

        binding.menuButton.setOnClickListener {
            bottomSheetShareBehavior.apply {
                state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        binding.sharePlaylist.setOnClickListener {
            shareDialog()
        }
        binding.deletePlaylist.setOnClickListener {
            bottomSheetShareBehavior.apply {
                state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить плейлист")
                .setMessage("Хотите удалить плейлист?")
                .setNeutralButton("Нет") { dialog, which ->
                }.setPositiveButton("Да") { dialog, which ->
                    viewModel.deletePlaylist(playlist)
                    findNavController().popBackStack()
                }.show()
        }
    }
    private fun shareDialog(){
        if (tracks.size==0){showDialog()}
    else{
        viewModel.onShareClicked(createInfo())

    }
    }


    private fun deleteTrack( track:Track){

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNeutralButton("Отмена") { dialog, which ->
            }.setPositiveButton("Удалить") { dialog, which ->
                viewModel.remove(track)
            }
        confirmDialog.show()
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
            .setNeutralButton("ОК",  object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                }
            })
            .show()
    }

    private fun createInfo():String{
        val tracks = adapter.tracks.toMutableList()?: emptyList<Track>()
        val playlistName = binding.playlistName.text
        val playlistDescription = binding.description.text
        val tracksNumber = binding.tracksNumber.text
        var description ="$playlistName\n$playlistDescription\n$tracksNumber"
        var trackN = 1
        for (i in tracks){
            description+= "\n$trackN.${i.artistName}-${i.trackName}(${ SimpleDateFormat("mm:ss", Locale.getDefault()).format(i.trackTimeMillis)})"
            trackN+=1

        }

        return description
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

