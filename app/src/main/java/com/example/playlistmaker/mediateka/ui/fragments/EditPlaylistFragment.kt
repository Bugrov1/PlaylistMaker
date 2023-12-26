package com.example.playlistmaker.mediateka.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.DialogStatus
import com.example.playlistmaker.mediateka.ui.viewmodel.CreatePlaylistViemodel
import com.example.playlistmaker.mediateka.ui.viewmodel.EditPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment : CreatePlaylistFragment() {

    private lateinit var playlist: Playlist
    private lateinit var oldName: String
    override var photoPath: Uri? = null

    companion object {
        private const val ARGS_ID = "id"

        fun createArgs(id: String): Bundle =
            bundleOf(
                ARGS_ID to id
            )

    }

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(
            requireArguments().getString(
                ARGS_ID
            )
        )
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

        viewModel.state.observe(viewLifecycleOwner) {
            renderButton(it)

        }
        viewModel.data.observe(viewLifecycleOwner) {
            playlist = it
            oldName = it.playlistName
            Log.v(" filepath", " 70 oldName is $oldName ")
            photoPath = it.filepath
            Log.v("filepath", " 72 photoPath is$photoPath")
            binding.createButton.text = "Сохранить"
            binding.EditTextName.setText(it.playlistName)
            binding.editTextDescription.setText(it.description)
            it.filepath?.let { it1 -> renderImage(it1) }

        }


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    renderImage(uri)
                    uriPhoto = uri

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            saveImageToPrivateStorage(uriPhoto)
            Log.v(" filepath", " 98 uriPhoto is $uriPhoto")
            val playlist = Playlist(
                id = playlist.id,
                playlistName = binding.EditTextName.text.toString(),
                description = descriptionEditText.text.toString(),
                filepath = photoPath,
                tracks = playlist.tracks,
                length = playlist.length
            )
            Log.v(" filepath", " 107 photoPath is $photoPath")
            viewModel.savePlaylist(playlist)

            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны.")
            .setNeutralButton("Отмена") { dialog, which ->

            }.setPositiveButton("Завершить") { dialog, which ->
                findNavController().popBackStack()
            }

        fun onBackPressed() {
            if (binding.EditTextName.text.toString().isNotEmpty()) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })


    }

    fun renderImage(uri: Uri) {
        Glide.with(requireActivity())
            .load(uri)
            .placeholder(R.drawable.placeholderbig)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .centerCrop()
            .into(binding.addPhoto)
    }


    override fun saveImageToPrivateStorage(uri: Uri?) {

        if (oldName != binding.EditTextName.text.toString() && uri != null) {

            val file = photoPath?.toFile()
            Log.v("filepath", "152 file is$file")
            if (file != null) {
                file.delete()
            }

        }

        if (uri != null) {
            val filePath = File(
                requireContext().getDir(
                    binding.EditTextName.text.toString(),
                    Context.MODE_PRIVATE
                ), "albums"
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