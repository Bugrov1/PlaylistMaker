package com.example.playlistmaker.mediateka.ui.fragments


import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.ButtonState
import com.example.playlistmaker.mediateka.ui.models.DialogStatus
import com.example.playlistmaker.mediateka.ui.viewmodel.CreatePlaylistViemodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {

    open val viewModel: CreatePlaylistViemodel by viewModel()
    private lateinit var backButton: ImageButton
    lateinit var binding: FragmentNewPlaylistBinding
    private lateinit var addPhoto: ImageButton
    lateinit var playlistName: String
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    open var photoPath: Uri? = null
    private lateinit var description: String
    lateinit var descriptionEditText: EditText
    var uriPhoto: Uri? = null
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
        addPhoto = binding.addPhoto
        backButton = binding.backButton
        descriptionEditText = binding.editTextDescription


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
                    Glide.with(requireActivity())
                        .load(uri)
                        .placeholder(R.drawable.placeholderbig)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into( binding.addPhoto)
                    uriPhoto = uri

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        var launchStatus: DialogStatus = DialogStatus.Hidden
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны.")
            .setNeutralButton("Отмена") { dialog, which ->
                launchStatus = DialogStatus.Hidden
            }.setPositiveButton("Завершить") { dialog, which ->
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                when (binding.EditTextName.text.toString().isNotEmpty()) {
                    true -> {
                        when (launchStatus) {
                            DialogStatus.Launched -> {
                                isEnabled = false
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }

                            DialogStatus.Neutral -> {
                                isEnabled = false
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }

                            else -> {
                                confirmDialog.show()
                                launchStatus = DialogStatus.Launched
                            }
                        }


                    }

                    false -> {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
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
            launchStatus = DialogStatus.Neutral
            requireActivity().onBackPressedDispatcher.onBackPressed()

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun renderButton(state: ButtonState) {
        when (state) {
            is ButtonState.Enabled -> binding.createButton.isEnabled = true
            is ButtonState.Disabled ->  binding.createButton.isEnabled = false


        }

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

    open fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri != null) {
            val filePath =
                File(requireContext().getDir( binding.EditTextName.text.toString(), MODE_PRIVATE), "albums")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val file = File(filePath,  binding.EditTextName.text.toString() + ".jpg")
            photoPath = file.toUri()
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        }

    }


}