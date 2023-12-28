package com.example.playlistmaker.player.ui.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.player.ui.BottomAdapter
import com.example.playlistmaker.player.ui.models.PlaylistState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private lateinit var track: Track
    private val roundingRadius = 10

    lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetRecycler: RecyclerView
    private val adapter = BottomAdapter()


    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)

    }

    val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    override fun onResume() {
        super.onResume()
        viewModel.refreshBottomSheet()
        Log.d("NAV", " Player onResume")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("NAV", " Player onViewCreated")


        val trackFromGson = requireArguments().getString(
            ARGS_TRACK
        )
        track = Gson().fromJson(trackFromGson, Track::class.java)

        initViews()
        setListeners()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state =
                BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.playlistLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                renderBottomSheet(it)
            }
        }

        viewModel.trackLiveData.observe(viewLifecycleOwner) {
            renderPlaylistStatus(it)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.playerState.collect {
                binding.playPauseButton.isEnabled = it.isPlayButtonEnabled
                buttonStatus(it.buttonText)
                binding.playTime.text = it.progress
                renderFavoritebutton(it.inFavorites)
                Log.v(ContentValues.TAG, "$it")
                Log.v(ContentValues.TAG, "timer ui ---${it.progress}")
            }
        }


    }

    fun setListeners() {
        binding.backArrow.setOnClickListener {
            viewModel.playerStop()
            findNavController().popBackStack(R.id.searchFragment,false)
//            requireActivity().onBackPressedDispatcher.onBackPressed()

        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playClickListen()
        }
        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.playlistButton.setOnClickListener {
            viewModel.refreshBottomSheet()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container_view, CreatePlaylistFragment())
//                .addToBackStack("playerActivity")
//                .commit()

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        adapter.onItemClick = {
            Log.d("PlayerActivity", "adapterclicked")
            viewModel.addToPlaylist(track.trackId, it)
            Toast.makeText(
                requireContext(),
                "Добавлено в плейлист ${it.playlistName}.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun initViews() {

        setupDetails(track)
        binding.playlistsRecycler.adapter = adapter


    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
        Log.d("PlayerActivity", "onCreate")
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    private fun renderBottomSheet(playlists: List<Playlist>) {
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()

    }

    private fun renderPlaylistStatus(state: PlaylistState) {
        when (state) {
            is PlaylistState.inPlaylist -> Toast.makeText(
                requireContext(),
                state.text,
                Toast.LENGTH_SHORT
            )
                .show()

            is PlaylistState.notInPlaylist -> {
                Toast.makeText(requireContext(), state.text, Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun setupDetails(
        track: Track
    ) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        binding.albumNameValue.text = track.collectionName ?: ""
        binding.yearValue.text = track.releaseDate?.subSequence(0, 4) ?: ""
        binding.genreValue.text = track.primaryGenreName ?: ""
        binding.countryValue.text = track.country ?: ""

        val albumCoverUrl =
            track.artworkUrl100
                .replaceAfterLast('/', "512x512bb.jpg")//done
        println(albumCoverUrl)

        Glide.with(this)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholderbig)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(binding.albumCover)
    }


    private fun buttonStatus(buttonStatus: String) {
        when (buttonStatus) {
            "PAUSE" -> binding.playPauseButton.setBackgroundResource(R.drawable.pausebutton)
            "PLAY" -> binding.playPauseButton.setBackgroundResource(R.drawable.playpausebutton)


        }
    }

    private fun renderFavoritebutton(inFavorites: Boolean) {
        if (inFavorites) {
            binding.likeButton.setBackgroundResource(R.drawable.likebuttontrue)
        }
        if (!inFavorites) {
            binding.likeButton.setBackgroundResource(R.drawable.likebutton)
        }

    }


}


