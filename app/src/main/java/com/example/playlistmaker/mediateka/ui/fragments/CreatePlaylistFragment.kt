package com.example.playlistmaker.mediateka.ui.fragments


import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.ButtonState
import com.example.playlistmaker.mediateka.ui.viewmodel.CreatePlaylistViemodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {

    val handler = Handler(Looper.getMainLooper())

    open val viewModel: CreatePlaylistViemodel by viewModel()
    lateinit var binding: FragmentNewPlaylistBinding

    lateinit var playlistName: String
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    open var photoPath: Uri? = null
    private lateinit var description: String


    var uriPhoto: Uri? = null


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createButton.isEnabled = false
        uriPhoto = null

        Log.d("uriPhoto", "${uriPhoto} ")

        viewModel.state.observe(viewLifecycleOwner) {
            renderButton(it)
        }

        playlistName = ""

        binding.EditTextName.doOnTextChanged { text, start, before, count ->
            viewModel.changeButtonstate(text.toString())
            if (text != null) {
                playlistName = text.toString()
            }

        }

        description = ""
        binding.editTextDescription.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                description = text.toString()
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    renderImage(uri)
                    uriPhoto = uri
                    initOnBackPress()
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finishCreatePlaylist))
            .setMessage(getString(R.string.allUnsavedWillbeLost))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->

            }.setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().popBackStack()
            }

        binding.backButton.setOnClickListener {
            onBackPressed(playlistName,
                description,
                uriPhoto)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed(playlistName
                    ,description,
                    uriPhoto)
            }
        })


        binding.createButton.setOnClickListener {
            saveImageToPrivateStorage(uriPhoto)
            val playlist = Playlist(
                id = null,
                playlistName = playlistName,
                description = description,
                filepath = photoPath, tracks = null,
                length = 0
            )
            viewModel.createPlaylist(playlist)

            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()

        }
    }
    private fun initOnBackPress(){
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed(playlistName
                    ,description,
                    uriPhoto)
            }
        })
    }

    open fun onBackPressed(text: String,textDesc: String,uri: Uri?) {

        if (text.isNotEmpty() or textDesc.isNotEmpty() or (uri!= null) ) {
            confirmDialog.show()
        } else {
            findNavController().popBackStack()

        }
    }

    fun renderButton(state: ButtonState) {
        when (state) {
            is ButtonState.Enabled -> binding.createButton.isEnabled = true
            is ButtonState.Disabled -> binding.createButton.isEnabled = false

        }
    }

    fun renderImage(uri: Uri) {
        val roundingRadius = 8
        Glide.with(requireActivity())
            .load(uri)
            .placeholder(R.drawable.placeholderbig)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(binding.addPhoto)
        binding.addPhoto.scaleType = ImageView.ScaleType.FIT_XY
    }

    open fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri != null) {
            val filePath =
                File(
                    requireContext().getDir(binding.EditTextName.text.toString(), MODE_PRIVATE),
                    "albums"
                )
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, binding.EditTextName.text.toString() + ".jpg")
            photoPath = file.toUri()
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        }
    }
}